package com.example.setups;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "form_details")
public class FormDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String label;

    private String inputType;

    @Column(name = "options")
    @org.hibernate.annotations.Type(JsonType.class)
    private HashMap<String, Object> option = new HashMap<>();

    @Transient
    private Map<Object, Object> keyValue = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_At")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @ManyToMany(mappedBy = "form")
      private List<Form> form;


}
