package com.jcaa.udec.entrypoint.rest.v1;

import com.jayway.jsonpath.JsonPath;
import com.jcaa.udec.adapter.cache.local.LocalCacheNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    private static final String USERS_PATH = "/api/v1/users";
    private static final String USER_PATH = USERS_PATH + "/{userId}";
    private static final String CREATE_REQUEST = """
            {
              "name": "Ada Lovelace",
              "email": "ada.lovelace@example.com"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        Cache userCache = Optional.ofNullable(cacheManager.getCache(LocalCacheNames.USERS)).orElseThrow();
        userCache.clear();
    }

    @Test
    void shouldCompleteUserCrudLifecycle() throws Exception {
        // Arrange
        String updateRequest = """
                {
                  "name": "  Ada   Byron  ",
                  "email": "ADA.BYRON@EXAMPLE.COM"
                }
                """;

        // Act
        MvcResult createResult = mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("http://localhost" + USERS_PATH + "/")))
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();
        String userId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        // Assert
        mockMvc.perform(get(USER_PATH, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada.lovelace@example.com"));
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.users[0].id").value(userId));

        // Act
        mockMvc.perform(put(USER_PATH, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Byron"))
                .andExpect(jsonPath("$.email").value("ada.byron@example.com"));
        mockMvc.perform(delete(USER_PATH, userId))
                .andExpect(status().isNoContent());

        // Assert
        mockMvc.perform(get(USER_PATH, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {
        // Arrange
        String invalidRequest = """
                {
                  "name": "",
                  "email": "not-an-email"
                }
                """;

        // Act
        // Assert
        mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    void shouldRejectDuplicatedEmail() throws Exception {
        // Arrange
        mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_REQUEST))
                .andExpect(status().isCreated());

        // Act
        // Assert
        mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_USER"));
    }

    @Test
    void shouldExposeOpenApiDocumentation() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/users']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/users/{userId}']").exists());
    }
}
