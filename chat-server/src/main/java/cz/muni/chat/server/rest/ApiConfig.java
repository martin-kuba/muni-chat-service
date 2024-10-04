package cz.muni.chat.server.rest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
// fix for paging
// see https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.page
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ApiConfig {

    private static final Logger log = LoggerFactory.getLogger(ApiConfig.class);

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        log.info("visit http://localhost:{}/", event.getWebServer().getPort());
    }

    /**
     * Adds shared definition to #/components/responses
     * that cannot be added using annotations.
     */
    @SuppressWarnings("Convert2Lambda")
    @Bean
    public OpenApiCustomizer openAPICustomizer() {
        return new OpenApiCustomizer() {
            @Override
            public void customise(OpenAPI openApi) {
                log.info("customising OpenAPI description with a shared response");

                openApi.getComponents().addResponses("SingleMessageResponse",
                        new ApiResponse()
                                .description("response containing a single message")
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ChatMessage"))
                                        )
                                )
                                .addLink("link_to_getMessage",
                                        new Link()
                                                .operationId("getMessage")
                                                .addParameter("id", "$response.body#/id")
                                                .description("""
                                                        The `id` value returned in the response can be used as
                                                        the `id` parameter in `GET /message/{id}`.
                                                        """)
                                )
                );
            }
        };
    }
}
