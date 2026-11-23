package com.campuslands.coworkhub.config;

import com.campuslands.coworkhub.presentation.dto.response.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";
    private static final String LOGIN_PATH = "/auth/login";

    @Bean
    public OpenAPI coworkHubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoWorkHub API")
                        .version("1.0.0")
                        .description("API REST para gestionar las sedes, salas, miembros y "
                                + "reservas de un coworking."))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    @Bean
    public OpenApiCustomizer errorResponsesCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            ModelConverters.getInstance().readAll(ErrorResponse.class)
                    .forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));

            openApi.getPaths().forEach((path, item) ->
                    item.readOperationsMap().forEach((method, operation) ->
                            addErrorResponses(path, method, operation)));
        };
    }

    @Bean
    public GroupedOpenApi authenticationGroup() {
        return GroupedOpenApi.builder()
                .group("autenticacion")
                .displayName("Autenticación")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi catalogsGroup() {
        return GroupedOpenApi.builder()
                .group("catalogos")
                .displayName("Catálogos")
                .pathsToMatch("/api/sedes/**", "/api/salas/**", "/api/equipamientos/**",
                        "/api/servicios-adicionales/**", "/api/planes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi membersGroup() {
        return GroupedOpenApi.builder()
                .group("miembros")
                .displayName("Miembros")
                .pathsToMatch("/api/miembros/**")
                .build();
    }

    @Bean
    public GroupedOpenApi availabilityGroup() {
        return GroupedOpenApi.builder()
                .group("disponibilidad")
                .displayName("Disponibilidad")
                .pathsToMatch("/api/disponibilidad/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bookingsGroup() {
        return GroupedOpenApi.builder()
                .group("reservas")
                .displayName("Reservas")
                .pathsToMatch("/api/reservas/**")
                .build();
    }

    private void addErrorResponses(String path, PathItem.HttpMethod method, Operation operation) {
        boolean isLogin = path.equals(LOGIN_PATH);
        boolean modifies = method != PathItem.HttpMethod.GET;
        boolean mayNotFound = path.contains("{") || path.equals("/api/disponibilidad");
        boolean hasInput = operation.getRequestBody() != null
                || (operation.getParameters() != null && !operation.getParameters().isEmpty());

        if (isLogin) {
            operation.setSecurity(List.of());
        }
        if (hasInput) {
            addError(operation, "400", "Datos faltantes o con formato inválido");
        }
        if (isLogin) {
            addError(operation, "401", "Usuario o contraseña incorrectos");
        } else {
            addError(operation, "401", "Sin token, o con un token alterado o vencido");
            addError(operation, "403", "El usuario no tiene permiso");
        }
        if (mayNotFound) {
            addError(operation, "404", "El recurso no existe");
        }
        if (modifies && !isLogin) {
            addError(operation, "409", "Se viola una regla de negocio o hay un duplicado");
        }
    }

    private void addError(Operation operation, String code, String description) {
        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }
        if (responses.containsKey(code)) {
            return;
        }
        responses.addApiResponse(code, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(
                                new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
    }
}