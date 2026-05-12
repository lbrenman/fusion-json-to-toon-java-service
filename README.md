# Amplify Fusion - JSON to TOON Converter Java Service

[Token-Oriented Object Notation (TOON)](https://github.com/toon-format/spec) is a compact, human-readable encoding of the JSON data model for LLM prompts.

If you are sending JSON data to your LLM, you can benefit from converting your JSON to TOON to (1) reduce token costs by anywhere from 20-60% and (2) reduce token count to not exceed any token per minute (TPM) rate limiting.

TOON provides a lossless serialization of the same objects, arrays, and primitives as JSON, but in a syntax that minimizes tokens and makes structure easy for models to follow.

# TOON Example

For example, the following JSON object:

```json
{
  "order": {
    "id": "ORD-20587",
    "customer": "Meridian Labs",
    "status": "shipped",
    "placedAt": "2025-11-02T14:30:00Z",
    "total": 12489.50,
    "items": [
      {
        "sku": "SEN-401",
        "name": "Thermal Sensor Array",
        "qty": 12,
        "unitPrice": 349.00,
        "category": "Sensors",
        "warehouse": "PDX-3"
      },
      {
        "sku": "MOD-218",
        "name": "RF Transceiver Module",
        "qty": 50,
        "unitPrice": 89.99,
        "category": "Communications",
        "warehouse": "PDX-3"
      },
      {
        "sku": "CAB-095",
        "name": "Shielded Data Cable 2m",
        "qty": 100,
        "unitPrice": 12.50,
        "category": "Cabling",
        "warehouse": "CHI-1"
      },
      {
        "sku": "PWR-330",
        "name": "48V Power Supply Unit",
        "qty": 6,
        "unitPrice": 274.00,
        "category": "Power",
        "warehouse": "CHI-1"
      },
      {
        "sku": "BRK-112",
        "name": "DIN Rail Mount Bracket",
        "qty": 25,
        "unitPrice": 8.40,
        "category": "Hardware",
        "warehouse": "PDX-3"
      }
    ],
    "shipments": [
      {
        "carrier": "FedEx",
        "tracking": "7749 2810 3347",
        "origin": "PDX-3",
        "destination": "Austin TX",
        "status": "in_transit"
      },
      {
        "carrier": "UPS",
        "tracking": "1Z999AA10123456784",
        "origin": "CHI-1",
        "destination": "Austin TX",
        "status": "delivered"
      }
    ]
  }
}
```

when converted to TOON looks like this:

```
order:
  id: ORD-20587
  customer: Meridian Labs
  status: shipped
  placedAt: 2025-11-02T14:30:00Z
  total: 12489.5
  items[5]{sku,name,qty,unitPrice,category,warehouse}:
    SEN-401,Thermal Sensor Array,12,349.0,Sensors,PDX-3
    MOD-218,RF Transceiver Module,50,89.99,Communications,PDX-3
    CAB-095,Shielded Data Cable 2m,100,12.5,Cabling,CHI-1
    PWR-330,48V Power Supply Unit,6,274.0,Power,CHI-1
    BRK-112,DIN Rail Mount Bracket,25,8.4,Hardware,PDX-3
  shipments[2]{carrier,tracking,origin,destination,status}:
    FedEx,7749 2810 3347,PDX-3,Austin TX,in_transit
    UPS,1Z999AA10123456784,CHI-1,Austin TX,delivered
```

You can see how compact and still highly readable the TOON format is.

# Fusion Java Service

The Amplify Fusion Java Service below is an implementation of a JSON to TOON conversion of Fusion Document and Document Array data. It is based on [JToon](https://github.com/toon-format/toon-java).

The Java Service shown below has two inputs, depending on your data set:

* `jsonObject` Document
* `jsonArray` Document Array

and two outputs:

* `error` String
* `toonOutput` String

All four must be added when you create your Java Service.

```java
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
```

The Java Service requires the following external libraries (uploaded to the data plane) so it cannot run on a shared data plane:

* [JToon 1.0.9](https://repo1.maven.org/maven2/dev/toonformat/jtoon/1.0.9/jtoon-1.0.9.jar)
* [Jackson Databind 3.0.4](https://repo1.maven.org/maven2/tools/jackson/core/jackson-databind/3.0.4/jackson-databind-3.0.4.jar)
* [Jackson Core 3.0.4](https://repo1.maven.org/maven2/tools/jackson/core/jackson-core/3.0.4/jackson-core-3.0.4.jar)
* [Jackson Annotations 3.0-rc5](https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/3.0-rc5/jackson-annotations-3.0-rc5.jar)
* [Jackson Module Blackbird 3.0.4](https://repo1.maven.org/maven2/tools/jackson/module/jackson-module-blackbird/3.0.4/jackson-module-blackbird-3.0.4.jar)

## Sample Amplify Fusion Project

An example test project is included in this repository. You can [import](https://docs.axway.com/bundle/amplify_integration/page/docs/manager_module/manage_the_environments/index.html#export-or-import-a-project) the zip file into your fusion tenant, and try the Java Service.