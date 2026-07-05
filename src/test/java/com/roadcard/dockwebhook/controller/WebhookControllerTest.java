package com.roadcard.dockwebhook.controller;

import com.roadcard.dockwebhook.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebhookService webhookService;

    @Test
    void receive_returns200_and_callsService() throws Exception {
        String body = "{\"test\":true}";
        doNothing().when(webhookService).processAsync(body);

        mockMvc.perform(post("/api/v1/webhooks/dock").contentType("application/json").content(body))
                .andExpect(status().isOk());

        verify(webhookService).processAsync(body);
    }
}
