package org.arthub.backend.ContollerTest;

import org.arthub.backend.Controller.FolderController;
import org.arthub.backend.DTO.FolderDTO;
import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Facade.FolderFacade;
import org.arthub.backend.Service.FolderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class FolderControllerTest {
    @Autowired
    private FolderController folderController;
    @MockBean
    private FolderService folderService;
    @MockBean
    private FolderFacade folderFacade;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;


    @Test
    public void testGetFolderById() throws Exception {
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderService.getFolderById(1L)).thenReturn(folder);
        ResponseEntity<?> response = folderController.getFolderById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(folder, response.getBody());
    }
    @Test
    public void testGetFoldersByUser() throws NotFound {
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderService.getFoldersByPatron()).thenReturn(List.of(folder));
        ResponseEntity<?> response = folderController.getFoldersByUser();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(List.of(folder), response.getBody());
    }
    @Test
    public void testCreateFolder() throws Exception {
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>(List.of(2L)));
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderFacade.createFolder(folderDTO)).thenReturn(folder);
        ResponseEntity<?> response = folderController.createFolder(folderDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(folder, response.getBody());
    }
    @Test
    public void testUpdateFolder() throws Exception {
        FolderDTO folderDTO = new FolderDTO("shrek_photos", " ", new ArrayList<>(List.of(2L)));
        Folder folder = new Folder(1L, "shrek_photos", " ", null, new ArrayList<>());
        when(folderFacade.updateFolder(1l, folderDTO)).thenReturn(folder);
        ResponseEntity<?> response = folderController.updateFolder(1L, folderDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(folder, response.getBody());
    }
    @Test
    public void testDeleteFolder() throws Exception {
        doNothing().when(folderFacade).deleteById(1L);
        ResponseEntity<?> response = folderController.deleteFolder(1L);
        assertEquals(200, response.getStatusCodeValue());
    }
}
