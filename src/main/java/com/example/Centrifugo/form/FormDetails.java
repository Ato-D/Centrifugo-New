package com.example.Centrifugo.form;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.CreationTimestamp;
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

    private String label;

    private String inputType;

//    @Column(name = "options")
//    @org.hibernate.annotations.Type(JsonType.class)
//    private HashMap<String, Object> option = new HashMap<>();

    @Transient
    private Map<Object, Object> keyValue = new HashMap<>();



    @ManyToMany(mappedBy = "formDetails", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    @HashCodeExclude
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