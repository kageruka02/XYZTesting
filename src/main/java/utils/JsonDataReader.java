package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for reading test data from JSON files under testdata/.
 * Returns structured data as Object[][] to be consumed by TestNG @DataProvider methods.
 */
public class JsonDataReader {

    private static final Logger log = LoggerFactory.getLogger(JsonDataReader.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TESTDATA_ROOT = "testdata/";

    private JsonDataReader() {}

    // ── Raw node access ─────────────────────────────────────────────────────

    /**
     * Parses the given JSON file and returns the node at the specified JSON pointer path.
     * Example: getNode("customers.json", "/validCustomers")
     */
    public static JsonNode getNode(String fileName, String jsonPointer) {
        try {
            JsonNode root = mapper.readTree(new File(TESTDATA_ROOT + fileName));
            return root.at(jsonPointer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + fileName + " at path " + jsonPointer, e);
        }
    }

    // ── TestNG DataProvider adapters ────────────────────────────────────────

    /**
     * Reads an array node and converts it to Object[][] for @DataProvider.
     * Each array element becomes one row; each field in the element becomes a column value.
     *
     * @param fileName    e.g. "customers.json"
     * @param jsonPointer e.g. "/validCustomers"
     * @param fields      field names to extract in order (become the columns)
     */
    public static Object[][] toDataProvider(String fileName, String jsonPointer, String... fields) {
        JsonNode array = getNode(fileName, jsonPointer);
        if (!array.isArray()) {
            throw new IllegalArgumentException(jsonPointer + " is not a JSON array in " + fileName);
        }

        List<Object[]> rows = new ArrayList<>();
        for (JsonNode item : array) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                JsonNode fieldNode = item.get(fields[i]);
                row[i] = fieldNode != null ? fieldNode.asText() : "";
            }
            rows.add(row);
        }
        log.debug("DataProvider loaded {} rows from {}{}", rows.size(), fileName, jsonPointer);
        return rows.toArray(new Object[0][]);
    }

    // ── Convenience methods for common data sets ─────────────────────────────

    public static Object[][] getValidCustomers() {
        return toDataProvider("customers.json", "/validCustomers",
                "firstName", "lastName", "postCode");
    }

    public static Object[][] getInvalidCustomers() {
        return toDataProvider("customers.json", "/invalidCustomers",
                "firstName", "lastName", "postCode", "scenario");
    }

    public static Object[][] getValidDeposits() {
        return toDataProvider("transactions.json", "/deposits/valid",
                "amount", "description");
    }

    public static Object[][] getInvalidDeposits() {
        return toDataProvider("transactions.json", "/deposits/invalid",
                "amount", "description");
    }

    public static Object[][] getValidWithdrawals() {
        return toDataProvider("transactions.json", "/withdrawals/valid",
                "depositFirst", "withdrawAmount", "expectedBalance", "description");
    }

    public static Object[][] getInsufficientWithdrawals() {
        return toDataProvider("transactions.json", "/withdrawals/insufficient",
                "depositFirst", "withdrawAmount", "description");
    }
}
