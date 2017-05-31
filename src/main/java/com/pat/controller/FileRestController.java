package com.pat.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.pat.domain.Evenement;
import com.pat.domain.FileUploaded;
import com.pat.domain.Member;
import com.pat.repo.EvenementsRepository;
import com.pat.repo.MembersRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;


/**
 * Created by patricou on 5/8/2017.
 */
@RestController
public class FileRestController {

    @Autowired
    private EvenementsRepository evenementsRepository;
    @Autowired
    private MembersRepository membersRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    private static final Logger log = LoggerFactory.getLogger(FileRestController.class);

    @RequestMapping( value = "/api/file/{fileId}", method = RequestMethod.GET,  produces = "application/pdf"  )
    public ResponseEntity< InputStreamResource> getFile(@PathVariable String fileId){

        // retrieve the file in MongoDB
        GridFSDBFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(gridFsFile.getContentType()));
        String filename = gridFsFile.getFilename();
        log.info("Request file " + filename);
        headers.setContentDispositionFormData(filename, filename);
        headers.set("Content-Disposition","inline; filename =" + filename);
        headers.set("Content-Length",Long.toString( gridFsFile.getLength()));

       return ResponseEntity.ok()
                .headers(headers)
                .body( new InputStreamResource(gridFsFile.getInputStream()));
    }

    @RequestMapping( value = "/uploadfile/{userId}/{evenementid}", method = RequestMethod.POST )
    // Important note : the name associate with RequestParam is 'file' --> seen in the browser network request.
    public ResponseEntity<FileUploaded> postFile(@RequestParam("file") MultipartFile filedata, @PathVariable String userId, @PathVariable String evenementid  ){
        log.info("Post file received, user.id : " +  userId +" / evenement.id : " + evenementid );

        try {
            Member uploaderMember = membersRepository.findOne(userId);

            DBObject metaData = new BasicDBObject();
            metaData.put("UploaderName", uploaderMember.getFirstName()+" "+uploaderMember.getLastName());
            metaData.put("UploaderId", uploaderMember.getId());

            // Save the doc ( all type ) in  MongoDB
            String fieldId =
                    gridFsTemplate.store( filedata.getInputStream(), filedata.getOriginalFilename(), filedata.getContentType(), metaData).getId().toString();
            log.info("Doc created id : "+fieldId);

            // create the file info
            FileUploaded fileUploaded = new FileUploaded(fieldId, filedata.getOriginalFilename(), filedata.getContentType(), uploaderMember);
            //find the evenement
            Evenement evenement = evenementsRepository.findOne(evenementid);
            evenement.getFileUploadeds().add(fileUploaded);
            // Save the evenement updated
            Evenement eventSaved = evenementsRepository.save(evenement);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(eventSaved.getId()).toUri());

            return new ResponseEntity<FileUploaded>(fileUploaded, httpHeaders, HttpStatus.CREATED);

        }catch (Exception e ){
            log.error(" Exception error " + e);
        }

        return new ResponseEntity<>(null,null,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @RequestMapping( value = "/api/file", method = RequestMethod.PUT )
    public ResponseEntity<Evenement> updateFile(@RequestBody Evenement evenement){

        log.info("Update file for evenement " + evenement.getId());

        // retrieve the evenement id ( with file to delete )
        Evenement evenementNotUpdated = evenementsRepository.findOne(evenement.getId());

        // retrieve the file id to delete
        FileUploaded f = evenementNotUpdated.getFileUploadeds().stream().filter(
                            fileUploaded -> {
                                boolean b = false;
                                for ( FileUploaded fileUploaded2 : evenement.getFileUploadeds())
                                    if ( fileUploaded.getFieldId().equals( fileUploaded2.getFieldId() )) {
                                        b = true;
                                        break;
                                }
                                return !b;
                            }
                        ).findFirst().get();

        log.info("File to delete " + f.getFieldId() );

        // update the evenement without the file ( the save erase all )
        Evenement savedEvenement = evenementsRepository.save(evenement);

        // delete the file in MongoDB
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(f.getFieldId())));

        // return the evenement
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(evenement.getId()).toUri());

        return new ResponseEntity<Evenement>(savedEvenement, httpHeaders, HttpStatus.CREATED);

    }

}
