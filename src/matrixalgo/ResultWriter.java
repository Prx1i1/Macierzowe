package matrixalgo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ResultWriter {

    String path = "";

    public ResultWriter(String path) {
        this.path = path;
    }

    public void writeResult(String filename, double[] data, int[][] operations){
        try {
            File file = new File(path + "/" + filename);
            file.createNewFile();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        try(FileWriter writer = new FileWriter(path + "/" + filename, true)){
            writer.write(
                    data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4] + "\t" +
                            (operations[0][0] + operations[0][1]) + "\t" +
                            (operations[1][0] + operations[1][1]) + "\t" +
                            (operations[2][0] + operations[2][1]) + "\t" +
                            (operations[3][0] + operations[3][1]) + "\n"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
