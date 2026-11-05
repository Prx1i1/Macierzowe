from z3 import *

def subterms(t):
    seen = {}
    def subterms_rec(t):
        if is_app(t):
            for ch in t.children():
                if ch in seen:
                    continue
                seen[ch] = True
                yield ch
                yield from subterms_rec(ch)
    return { s for s in subterms_rec(t) }

def are_equal(s, t1, t2):
    s.push()
    s.add(t1 != t2)
    r = s.check()
    s.pop()
    return r == unsat

def simplify(slv, mdl, t):
    subs = subterms(t)
    values = { s : mdl.eval(s) for s in subs }
    values[t] = mdl.eval(t)
    def simplify_rec(t):
        subs = subterms(t)
        for s in subs:
            if s.sort().eq(t.sort()) and values[s].eq(values[t]) and are_equal(slv, s, t):
                return simplify_rec(s)
        chs = [simplify_rec(ch) for ch in t.children()]
        return t.decl()(chs)
    return simplify_rec(t)

MAT_SIZE = 3

varmat = [[Real(f'A{y+1}{x+1}') for x in range(MAT_SIZE)] for y in range(MAT_SIZE)]
m = [varmat[y].copy() + [1 if x == y else 0 for x in range(MAT_SIZE)] for y in range(MAT_SIZE)]

sol = Solver()
for r in [c for r in varmat for c in r]:
    sol.add(r != 0)
sol.check()
mod = sol.model()

for i in range(len(m)):
    print(f'i {i}')
    div = m[i][i]
    div = div ** -1
    for n in range(len(m[i])):
        m[i][n] = simplify(sol, mod, div * m[i][n])
    for j in range(len(m)):
        if i == j: continue
        print(f'j {j}')
        mul = m[j][i]
        for n in range(3 if i == 2 and j == 1 else 0, len(m[j])):
            print(f'n {n}')
            m[j][n] = simplify(sol, mod, m[j][n] - mul * m[i][n])

max_col_width = [0 for _ in range(MAT_SIZE * 2)]

def eq_to_str(eq, col):
    eq_str = str(eq).replace("\n", "")
    max_col_width[col] = max(len(eq_str), max_col_width[col])
    return eq_str

mstr = [[eq_to_str(cell, i) for i, cell in enumerate(row)] for row in m]

for row in mstr:
    for i, cell in enumerate(row):
        if i < MAT_SIZE: continue
        print(cell.ljust(max_col_width[i]), end=' | ')
    print()

def int_to_label(n):
    label = ''
    while True:
        rem = n % 26
        label = chr(0x41 + rem) + label
        n = n // 26
        if n == 0: break
    return label

counter = 1

term_labels = {}
term_seen_count = {}
label_terms = {}
label_stub = {}

def ingest(term: ArithRef):
    global counter
    if term in term_labels:
        term_seen_count[term] += 1
        return term_labels[term]
    child_labels = [ingest(ch) if is_app(ch) else ch for ch in term.children()]
    our_label = int_to_label(counter)
    counter += 1
    term_labels[term] = our_label
    term_seen_count[term] = 1
    label_terms[our_label] = term
    label_stub[our_label] = (term.kind(), child_labels)
    return our_label

toplevel_labels = []

for row in m:
    for i, cell in enumerate(row):
        if i < MAT_SIZE: continue
        toplevel_labels.append(ingest(cell))

def expand(label, force=False):
    kind, children = label_stub[label]
    if not force and term_seen_count[label_terms[label]] > 1 and kind != Z3_OP_ANUM and kind != 45102:
        return Real(label)

    ec = [expand(ch) for ch in children]
    match kind:
        case 518:
            return ec[0] + ec[1]
        case 519:
            return ec[0] - ec[1]
        case 521:
            return ec[0] * ec[1]
        case 529:
            return ec[0] ** ec[1]
        case 512 | 45102:
            return label_terms[label]
        case _:
            raise Exception(kind)

print(toplevel_labels)
# print(term_labels)
# print(term_seen_count)
# print(label_stub)

for label, term in label_terms.items():
    kind, _ = label_stub[label]
    if term_seen_count[term] > 1 and kind != Z3_OP_ANUM and kind != 45102:
        print(f'{label} = {expand(label, True)}')

print()

for i, toplabel in enumerate(toplevel_labels):
    print(f'{i}. {expand(toplabel)}')
