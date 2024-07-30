package com.example.Centrifugo.form;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;





@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
@Table(name = "form", schema = "centrifugo")
@Entity
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Version
    @Column(name = "version")
    private int version = 1;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "form_form_details",
            joinColumns = {@JoinColumn(name = "form_id")},
            inverseJoinColumns = {@JoinColumn(name = "form_details_id")}
    )
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FormDetails> formDetails;

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




//    label: Type
//    options: [
//    {"setup": "setup_types_category_id", "valueType": "ID"}
//]
//    inputType: "DROPDOWN"
//    key: "typeId"



}
