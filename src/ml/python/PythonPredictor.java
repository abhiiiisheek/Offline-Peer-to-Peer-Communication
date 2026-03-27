package ml.python;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class PythonPredictor {

    public List<String> getReplies(String message) {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python", "python/predict.py", message
            );

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String output;
            StringBuilder finalOutput = new StringBuilder();

            while ((output = reader.readLine()) != null) {
                finalOutput.append(output);
            }

            String result = finalOutput.toString();
            result = result.replaceAll("[^a-zA-Z0-9 ,.!?']", "");

            if (result != null && !result.isEmpty()) {
                return Arrays.asList(result.split(","));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Arrays.asList("Okay", "Nice", "Got it");
    }
}