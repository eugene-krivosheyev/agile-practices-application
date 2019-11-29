package com.acme.dbo.account.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Past;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class Account {
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(dataType = "Long")
    @EqualsAndHashCode.Exclude
    @Nullable Long id;

    @Column(name = "AMOUNT", columnDefinition = "DECIMAL(31, 16)")
    @ApiModelProperty(dataType = "String", required = true)
    @NonNull Double amount;

    @Column(name = "CREATE_STAMP")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Instant")
    @EqualsAndHashCode.Exclude
    @Nullable @Past Instant createStamp;

    @Column(name = "CLIENT_ID", columnDefinition = "INTEGER")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @NonNull Long clientId;
}
