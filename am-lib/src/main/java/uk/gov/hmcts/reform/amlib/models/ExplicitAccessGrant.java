package uk.gov.hmcts.reform.amlib.models;

import com.fasterxml.jackson.core.JsonPointer;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.amlib.enums.AccessorType;
import uk.gov.hmcts.reform.amlib.enums.Permission;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public final class ExplicitAccessGrant {
    @NotBlank
    private final String resourceId;
    @NotNull
    @Valid
    private final ResourceDefinition resourceDefinition;
    @NotEmpty
    private final Set<@NotBlank String> accessorIds;
    @NotNull
    private final AccessorType accessorType;
    @NotEmpty
    private final Map<@NotNull JsonPointer, @NotEmpty Set<@NotNull Permission>> attributePermissions;
    @NotBlank
    private final String relationship;
}
