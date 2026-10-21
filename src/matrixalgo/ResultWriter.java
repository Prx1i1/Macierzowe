package matrixalgo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResultWriter {

    String path = "";

    public ResultWriter(String path) {
        this.path = path;
    }

    public void writeResult(String filename, double[] data){
        try {
            File file = new File(path + "/" + filename);
            file.createNewFile();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        try(FileWriter writer = new FileWriter(path + "/" + filename, true)){
            writer.write(
                    data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4] + "\n"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
