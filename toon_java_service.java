import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
// Please do not change pre-populated code.
public class TOONJavaServiceTest_TOONConverter {

    public Map<String,Object> execute(Map<String,Object> dataIn) {
        Map<String,Object> dataOut = new HashMap<String, Object>();

/* Please start your code here */

        try {
            // Read both inputs — caller populates one or the other
            Object jsonObjectInput = dataIn.get("jsonObject");
            Object jsonArrayInput = dataIn.get("jsonArray");

            if (jsonObjectInput == null && jsonArrayInput == null) {
                dataOut.put("error", "At least one input must be provided: jsonObject or jsonArray");
                dataOut.put("toonOutput", "");
                return dataOut;
            }

            // Use Jackson ObjectMapper (fully qualified to avoid import validation)
            // Jackson 3.x is a transitive dependency of JToon and will be on the dedicated data plane classpath
            tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
            String jsonString;

            if (jsonObjectInput != null) {
                // Input is a Document (Map<String, Object>)
                jsonString = mapper.writeValueAsString(jsonObjectInput);
            } else {
                // Input is a Document Array (List<Map<String, Object>>)
                jsonString = mapper.writeValueAsString(jsonArrayInput);
            }

            // Use JToon (fully qualified) to convert JSON string to TOON format
            String toonResult = dev.toonformat.jtoon.JToon.encodeJson(jsonString);

            dataOut.put("toonOutput", toonResult);
            dataOut.put("error", "");

        } catch (Exception e) {
            dataOut.put("error", "Conversion failed: " + e.getMessage());
            dataOut.put("toonOutput", "");
        }

/* your code ends here */
        return dataOut;
    }
}