package uk.gov.hmcts.reform.amlib;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.amlib.FilterServiceTest.Resource.Address;
import uk.gov.hmcts.reform.amlib.FilterServiceTest.Resource.Claimant;
import uk.gov.hmcts.reform.amlib.FilterServiceTest.Resource.Defendant;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.CREATE_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestConstants.READ_PERMISSION;
import static uk.gov.hmcts.reform.amlib.helpers.TestDataFactory.createPermissions;

class FilterServiceTest {

    private final ObjectMapper mapper = new ObjectMapper()
        .setSerializationInclusion(Include.NON_NULL);
    private final FilterService fs = new FilterService();

    @Test
    void itShouldBePossibleToSHowEverything() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        JsonNode returnedJson = fs.filterJson(inputJson, createPermissions("", READ_PERMISSION));

        assertThat(returnedJson).isEqualTo(inputJson);
    }

    @Test
    void itShouldBePossibleToShowEverythingExceptTopLevelValue() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf(""), READ_PERMISSION)
            .put(JsonPointer.valueOf("/amount"), CREATE_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .claimant(Claimant.builder()
                    .name("John")
                    .age(21)
                    .address(Address.builder()
                        .city("London")
                        .postcode("SE1")
                        .build())
                    .build()
                )
                .defendant(Defendant.builder()
                    .name("Marry")
                    .address(Address.builder()
                        .city("Swansea")
                        .postcode("SA1")
                        .build())
                    .build())
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowEverythingExceptTopLevelObject() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf(""), READ_PERMISSION)
            .put(JsonPointer.valueOf("/claimant"), CREATE_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .defendant(Defendant.builder()
                    .name("Marry")
                    .address(Address.builder()
                        .city("Swansea")
                        .postcode("SA1")
                        .build())
                    .build())
                .amount(100)
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowEverythingExceptLeafLevelValue() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf(""), READ_PERMISSION)
            .put(JsonPointer.valueOf("/claimant/age"), CREATE_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .claimant(Claimant.builder()
                    .name("John")
                    .address(Address.builder()
                        .city("London")
                        .postcode("SE1")
                        .build())
                    .build()
                )
                .defendant(Defendant.builder()
                    .name("Marry")
                    .address(Address.builder()
                        .city("Swansea")
                        .postcode("SA1")
                        .build())
                    .build())
                .amount(100)
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowEverythingExceptLeafLevelObject() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf(""), READ_PERMISSION)
            .put(JsonPointer.valueOf("/claimant/address"), CREATE_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .claimant(Claimant.builder()
                    .name("John")
                    .age(21)
                    .build()
                )
                .defendant(Defendant.builder()
                    .name("Marry")
                    .address(Address.builder()
                        .city("Swansea")
                        .postcode("SA1")
                        .build())
                    .build())
                .amount(100)
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowTopLevelValueOnly() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf("/amount"), READ_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .amount(100)
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowTopLevelObjectOnly() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf("/claimant"), READ_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .claimant(Claimant.builder()
                    .name("John")
                    .age(21)
                    .address(Address.builder()
                        .city("London")
                        .postcode("SE1")
                        .build())
                    .build())
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowLeafLevelValueOnly() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf("/claimant/age"), READ_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .claimant(Claimant.builder()
                    .age(21)
                    .build()
                )
                .build()
            )
        );
    }

    @Test
    void itShouldBePossibleToShowLeafLevelObjectOnly() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
            .put(JsonPointer.valueOf("/claimant/address"), READ_PERMISSION)
            .build();

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(mapper.valueToTree(
            Resource.builder()
                .claimant(Claimant.builder()
                    .address(Address.builder()
                        .city("London")
                        .postcode("SE1")
                        .build())
                    .build()
                )
                .build()
            )
        );
    }

//    @Test
//    void itShouldBePossibleToShowEverythingExceptOnePropertyOtherThanProperty() throws IOException {
//        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));
//
//        Map<JsonPointer, Set<Permission>> attributePermissions = ImmutableMap.<JsonPointer, Set<Permission>>builder()
//            .put(JsonPointer.valueOf(""), READ_PERMISSION)
//            .put(JsonPointer.valueOf("/address"), CREATE_PERMISSION)
//            .put(JsonPointer.valueOf("/address/city"), READ_PERMISSION)
//            .build();
//
//        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);
//
//        assertThat(returnedJson).isEqualTo(objectNode(node -> node
//            .put("name", "John")
//            .put("age", 18)
//            .putObject("address")
//            .put("city", "London")
//        ));
//    }

    @Test
    void itShouldBePossibleToHideEverything() throws IOException {
        JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/input.json"));

        JsonNode returnedJson = fs.filterJson(inputJson, createPermissions("/name", CREATE_PERMISSION));

        assertThat(returnedJson).isNull();
    }

    // REVIEW BELOW CASES

    @Test
    void filterNestedJsonWhenRootExplicitlyHasNoReadButAnAttributeDoes() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf("/child"), CREATE_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName"), READ_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void filterNestedJsonWithoutExplicitReadRemovesExpectedAttributes() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf("/child/childName"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childAge"), CREATE_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Test
    void filterComplexNestedJson() throws IOException {
        final JsonNode inputJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestComplexInput.json"));
        final JsonNode expectedJson = mapper.readTree(ClassLoader.getSystemResource("FilterServiceResources/filterServiceTestComplexExpected.json"));

        final Map<JsonPointer, Set<Permission>> attributePermissions = new ConcurrentHashMap<>();
        attributePermissions.put(JsonPointer.valueOf("/child"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childAge"), CREATE_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName/firstName"), READ_PERMISSION);
        attributePermissions.put(JsonPointer.valueOf("/child/childName/lastName"), CREATE_PERMISSION);

        JsonNode returnedJson = fs.filterJson(inputJson, attributePermissions);

        assertThat(returnedJson).isEqualTo(expectedJson);
    }

    @Builder
    @Data
    static class Resource {
        private final Claimant claimant;
        private final Defendant defendant;
        private final Integer amount;

        @Data
        @Builder
        static class Claimant {
            private final String name;
            private final Integer age;
            private final Address address;
        }

        @Data
        @Builder
        static class Defendant {
            private final String name;
            private final Address address;
        }

        @Data
        @Builder
        static class Address {
            private final String city;
            private final String postcode;
        }
    }
}
