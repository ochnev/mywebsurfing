package com.mywebsurfing.service;

import com.mywebsurfing.entity.AppUser;
import com.mywebsurfing.entity.Bookmark;
import com.mywebsurfing.entity.Folder;
import com.mywebsurfing.entity.LinkCollection;
import com.mywebsurfing.entity.Realm;
import com.mywebsurfing.repository.AppUserRepository;
import com.mywebsurfing.repository.BookmarkRepository;
import com.mywebsurfing.repository.FolderRepository;
import com.mywebsurfing.repository.LinkCollectionRepository;
import com.mywebsurfing.repository.RealmRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class DataImportServiceTest {

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RealmRepository realmRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private LinkCollectionRepository linkCollectionRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    private final AppUser defaultUser = new AppUser(null,
            "Default User",
            "user1",
            "secure123",
            "nobody@nowhere.com",
            null);

    @BeforeEach
    private void setup() {
        appUserRepository.save(defaultUser);
    }

    @Test
    void testImportData() {
        String csvData = "IT;Blockchain;https://www.npmjs.com/package/create-eth-app;Create Eth App\n" +
                "IT;;https://github.com/crytic/awesome-ethereum-security;Awesome Ethereum Security\n" +
                "IT;Blockchain and Java;https://ethereum.org/en/developers/docs/programming-languages/java/;Ethereum for Java developers\n" +
                "Languages;German;https://www.youtube.com/watch?v=RuGmc662HDg&list=PLF9mJC4RrjIhS4MMm0x72-qWEn1LRvPuW;German for beginners - A1";
        dataImportService.importData(csvData, defaultUser);

        List<Realm> realms = realmRepository.findAll();
        List<Realm> realmsForUser = realmRepository.findByAppUser(defaultUser);
        List<Folder> folders = folderRepository.findAll();
        List<LinkCollection> linkCollections = linkCollectionRepository.findAll();
        List<Bookmark> bookmarks = bookmarkRepository.findAll();

        assertEquals(2, realms.size());
        Realm realm1 = realms.get(0);
        Realm realm2 = realms.get(1);
        assertEquals("IT", realm1.getName());
        assertEquals(defaultUser.getId(), realm1.getAppUser().getId());
        assertEquals("Languages", realm2.getName());
        assertEquals(defaultUser.getId(), realm2.getAppUser().getId());

        assertEquals(2, realmsForUser.size());
        Realm realm1u = realmsForUser.get(0);
        Realm realm2u = realmsForUser.get(1);
        assertEquals(defaultUser.getId(), realm1u.getAppUser().getId());
        assertEquals("IT", realm1u.getName());
        assertEquals(defaultUser.getId(), realm1u.getAppUser().getId());
        assertEquals("Languages", realm2u.getName());

        assertEquals(3, folders.size());
        Folder folder1 = folders.get(0);
        Folder folder2 = folders.get(1);
        Folder folder3 = folders.get(2);
        assertEquals(realm1u.getId(), folder1.getRealm().getId());
        assertEquals("Blockchain", folder1.getName());
        assertEquals(realm1u.getId(), folder2.getRealm().getId());
        assertEquals("Blockchain and Java", folder2.getName());
        assertEquals(realm2u.getId(), folder3.getRealm().getId());
        assertEquals("German", folder3.getName());

        assertEquals(1, linkCollections.size());
        LinkCollection linkCollection1 = linkCollections.get(0);
        assertEquals(realm1.getId(), linkCollection1.getRealm().getId());
        assertEquals("https://github.com/crytic/awesome-ethereum-security", linkCollection1.getUrl());
        assertEquals("Awesome Ethereum Security", linkCollection1.getName());

        assertEquals(3, bookmarks.size());
        Bookmark bookmark1 = bookmarks.get(0);
        Bookmark bookmark2 = bookmarks.get(1);
        Bookmark bookmark3 = bookmarks.get(2);
        assertEquals(folder1.getId(), bookmark1.getFolder().getId());
        assertEquals("https://www.npmjs.com/package/create-eth-app", bookmark1.getUrl());
        assertEquals("Create Eth App", bookmark1.getTitle());
        assertEquals(folder2.getId(), bookmark2.getFolder().getId());
        assertEquals("https://ethereum.org/en/developers/docs/programming-languages/java/", bookmark2.getUrl());
        assertEquals("Ethereum for Java developers", bookmark2.getTitle());
        assertEquals(folder3.getId(), bookmark3.getFolder().getId());
        assertEquals("https://www.youtube.com/watch?v=RuGmc662HDg&list=PLF9mJC4RrjIhS4MMm0x72-qWEn1LRvPuW", bookmark3.getUrl());
        assertEquals("German for beginners - A1", bookmark3.getTitle());
    }

    @AfterEach
    private void tearDown() {
        bookmarkRepository.deleteAll();
        folderRepository.deleteAll();
        linkCollectionRepository.deleteAll();
        realmRepository.deleteAll();
        appUserRepository.deleteAll();
    }

}
