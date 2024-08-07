package com.example.Centrifugo.enums;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public enum Constraints {
        LESS_THAN_8("Has to be less than 8 characters", "lessThan8"),
        NO_SPECIAL_CHAR("No special characters allowed", "noSpecialChar"),
        MORE_THAN_8("Has to be more than 8 characters", "moreThan8");

        private final String label;
        private final String value;

        Constraints(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

//    MINIMUM,
//    MAXIMUM,
//    NO_SPECIAL_CHARACTERS

//    LESS_THAN_8("Has to be less than 8 characters"),
//    NO_SPECIAL_CHAR("No special character is allowed"),
//    MORE_THAN_8("Has to be more than 8 characters");
//
//    private final String representation;
//
//    Constraints(String representation) {
//        this.representation = representation;
//    }
//
//    public String getRepresentation() {
//        return representation;
//    }
//
//    public static Map<String, String> getKeyValues() {
//        Map<String, String> pairs = new HashMap<>();
//        List<String> representations = new ArrayList<>();
//        for (Constraints constraints : Constraints.values()) {
//            representations.add(constraints.getRepresentation());
//            pairs.put(String.valueOf(constraints), String.valueOf(constraints.getRepresentation()));
//        }
//        return pairs;
//    }
//
//    private static final Map<String, Constraints> REPRESENTATION_MAP = new HashMap<>();
//
//    static {
//        for (Constraints constraints : Constraints.values()) {
//            REPRESENTATION_MAP.put(constraints.getRepresentation().toUpperCase(), constraints);
//        }
//    }
//
//    public static Constraints valueOfRepresentation(String representation) {
//        return REPRESENTATION_MAP.get(representation.toUpperCase());
//    }
}




