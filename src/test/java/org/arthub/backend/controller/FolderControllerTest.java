package org.arthub.backend.controller;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.FolderDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.facade.FolderFacade;
import org.arthub.backend.service.FolderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class FolderControllerTest {
    @Autowired
    private FolderController folderController;
    @MockBean
    private FolderService folderService;
    @MockBean
    private FolderFacade folderFacade;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;


    @Test
     void testGetFolderById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderService.getFolderById(1L)).thenReturn(folder);
        ResponseEntity<?> response = folderController.getFolderById(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(folder, response.getBody());
    }
    @Test
     void testGetFoldersByUser() throws NotFound {
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderService.getFoldersByPatron()).thenReturn(List.of(folder));
        ResponseEntity<?> response = folderController.getFoldersByUser();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(folder), response.getBody());
    }
    @Test
     void testCreateFolder() throws Exception {
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>(List.of(2L)));
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderFacade.createFolder(folderDTO)).thenReturn(folder);
        ResponseEntity<?> response = folderController.createFolder(folderDTO);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(folder, response.getBody());
    }
    @Test
     void testUpdateFolder() throws Exception {
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>(List.of(2L)));
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderFacade.updateFolder(1l, folderDTO)).thenReturn(folder);
        ResponseEntity<?> response = folderController.updateFolder(1L, folderDTO);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(folder, response.getBody());
    }
    @Test
     void testDeleteFolder() throws Exception {
        doNothing().when(folderFacade).deleteById(1L);
        ResponseEntity<?> response = folderController.deleteFolder(1L);
        assertEquals(200, response.getStatusCode().value());
    }
}
