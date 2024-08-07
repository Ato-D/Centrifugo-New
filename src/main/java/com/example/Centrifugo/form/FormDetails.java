package com.example.Centrifugo.form;

import com.example.Centrifugo.enums.Constraints;
import com.example.Centrifugo.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "form_details", schema = "centrifugo")
@Entity
public class FormDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int index;

    private String fieldLabel;

    @Column(name = "options", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(JsonType.class)
    private List<Map<String, String>> fieldOptions;

    private Boolean isRequired;

    private String defaultValue;

    private String placeholder;

    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

//    @Enumerated(EnumType.STRING)
    @Column(name = "constraints", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(JsonType.class)
    private List<Constraints> constraints;

//    //    @Enumerated(EnumType.STRING)
//    @Column(name = "constraints", columnDefinition = "jsonb")
//    @org.hibernate.annotations.Type(JsonType.class)
//    private Object constraints;

    private String key;

    @ManyToMany(mappedBy = "formDetails", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Form> form;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_At")
    private ZonedDateTime createdAt = ZonedDateTime.now();


    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private ZonedDateTime updatedAt = ZonedDateTime.now();


}