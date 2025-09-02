package com.aiplus.backend.docker.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.aiplus.backend.docker.exceptions.DockerAuthenticationException;
import com.aiplus.backend.docker.exceptions.DockerHubApiException;
import com.aiplus.backend.docker.exceptions.DockerImageNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DockerImageVerifier {

    private final RestTemplate restTemplate;

    /**
     * Checks if a Docker image exists in the specified user's repository.
     *
     * @param dockerUser the Docker username
     * @param dockerPat  the Docker personal access token
     * @param imageName  the name of the image to check
     * @return true if the image exists, false otherwise
     */
    public boolean existsImage(String dockerUser, String dockerPat, String imageName) {
        String jwt = loginAndGetJwt(dockerUser, dockerPat);

        String url = String.format("https://hub.docker.com/v2/repositories/%s/%s", dockerUser, imageName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
            // si on arrive là, le repo existe
            return resp.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new DockerImageNotFoundException(imageName);
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new DockerAuthenticationException("Invalid Docker credentials for user " + dockerUser);
            }
            throw new DockerHubApiException("Client-side error during Docker image check", e);
        } catch (RestClientException e) {
            throw new DockerHubApiException("Error contacting Docker Hub API", e);
        }
    }

    /**
     * Logs in to Docker Hub and retrieves a JWT token.
     *
     * @param username the Docker username
     * @param pat      the Docker personal access token
     * @return the JWT token
     */
    private String loginAndGetJwt(String username, String pat) {
        try {

            String loginUrl = "https://hub.docker.com/v2/users/login/";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> creds = Map.of("username", username, "password", pat);
            HttpEntity<Map<String, String>> loginRequest = new HttpEntity<>(creds, headers);
            Map body = restTemplate.postForObject(loginUrl, loginRequest, Map.class);
            if (body == null || body.get("token") == null) {
                throw new DockerAuthenticationException(
                        "Cannot authenticate to Docker Hub,Check your Docker Hub credentialsand try again");
            }
            return body.get("token").toString();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new DockerAuthenticationException("Invalid Docker credentials for user " + username);
            }
            System.out.println("Login failed: " + e.getMessage());

            throw new DockerAuthenticationException("Could not authenticate to Docker Hub", e);
        } catch (RestClientException e) {
            throw new DockerHubApiException("Error during Docker Hub authentication :" + e.getMessage());
        }
    }
}
