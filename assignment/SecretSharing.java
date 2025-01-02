import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SecretSharing {

    public static void main(String[] args) {
        try (JsonReader reader = Json.createReader(new FileReader("src/input.json"))) {
            JsonObject inputJson = reader.readObject();

            // Read keys
            JsonObject keys = inputJson.getJsonObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");


            JsonObject roots = inputJson.getJsonObject("roots");
            if (roots == null) {
                throw new IllegalArgumentException("Error: 'roots' key is missing or null in the input JSON file.");
            }
            Map<Integer, BigInteger> points = new HashMap<>();
            for (String key : roots.keySet()) {
                int x = Integer.parseInt(key);
                JsonObject root = roots.getJsonObject(key);
                int base = Integer.parseInt(root.getString("base"));
                BigInteger y = new BigInteger(root.getString("value"), base);
                points.put(x, y);
            }

            BigInteger secret = lagrangeInterpolation(points, BigInteger.ZERO);
            System.out.println("Secret (c) = " + secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        for (Map.Entry<Integer, BigInteger> p1 : points.entrySet()) {
            BigInteger term = p1.getValue();
            for (Map.Entry<Integer, BigInteger> p2 : points.entrySet()) {
                if (!p1.getKey().equals(p2.getKey())) {
                    BigInteger numerator = x.subtract(BigInteger.valueOf(p2.getKey()));
                    BigInteger denominator = BigInteger.valueOf(p1.getKey()).subtract(BigInteger.valueOf(p2.getKey()));
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
