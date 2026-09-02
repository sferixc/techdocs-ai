package com.eric.techdocsai.document;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DocumentControllerTest {
    private DocumentSearchService service;
    private MockMvc mvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        service = mock(DocumentSearchService.class);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(
                        new DocumentController(mock(DocumentRepository.class), service))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void closeValidator() {
        validator.close();
    }

    @Test
    void returnsSearchResultsAsJson() throws Exception {
        when(service.search("What is overfitting?"))
                .thenReturn(List.of(new DocumentSearchResult(
                        1L, "Useful ML", 12L, "Overfitting reduces generalization.", 2, 3, 0.8)));

        mvc.perform(post("/api/documents/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"What is overfitting?\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].documentId").value(1))
                .andExpect(jsonPath("$[0].documentTitle").value("Useful ML"))
                .andExpect(jsonPath("$[0].chunkId").value(12))
                .andExpect(jsonPath("$[0].content").value("Overfitting reduces generalization."))
                .andExpect(jsonPath("$[0].startPage").value(2))
                .andExpect(jsonPath("$[0].endPage").value(3))
                .andExpect(jsonPath("$[0].similarity").value(0.8))
                .andExpect(jsonPath("$[0].embedding").doesNotExist());

        verify(service).search("What is overfitting?");
    }
}
