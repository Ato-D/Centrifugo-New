package com.example.Centrifugo.form;

import com.example.Centrifugo.enums.Constraints;
import com.example.Centrifugo.enums.InputType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Type;
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
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class FormDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String label;

    @Enumerated(EnumType.STRING)
    private InputType inputType;

    @Column(name = "options", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(JsonType.class)
    private Map<String, Object> option = new HashMap<>();

//    @Column(name = "options")
//    @org.hibernate.annotations.Type(JsonType.class)
//    private HashMap<String, Object> options = new HashMap<>();



    private String key;

    @Enumerated(EnumType.STRING)
    private Constraints constraints;

    @ManyToMany(mappedBy = "formDetails", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
//    @HashCodeExclude
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Form> form;

    @CreationTimestamp
    @Column(name = "created_At")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private ZonedDateTime updatedAt = ZonedDateTime.now();


}