package com.example.setups;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "form")
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "version")
    private int version = 1;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "FormDetails",
            joinColumns = { @JoinColumn(name = "form_id") },
            inverseJoinColumns = { @JoinColumn(name = "form_details_id") }
    )
    private List<FormDetails>  formDetails;

    @CreationTimestamp
    @Column(name = "created_At")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private ZonedDateTime updatedAt = ZonedDateTime.now();
}
