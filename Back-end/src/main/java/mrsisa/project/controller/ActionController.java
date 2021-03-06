package mrsisa.project.controller;


import mrsisa.project.dto.ActionDTO;
import mrsisa.project.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/action")
public class ActionController {
    @Autowired
    private ActionService actionService;

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ROLE_COTTAGE_OWNER','ROLE_BOAT_OWNER', 'INSTRUCTOR')")
    public ResponseEntity<String> addAction(@RequestBody ActionDTO actionDTO) throws IOException {
            if (actionService.add(actionDTO)) return ResponseEntity.status(HttpStatus.CREATED).body("Success");
            else return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Action must be created within available period!");
    }

    @GetMapping(value="/getActions/{id}")
    public ResponseEntity<List<ActionDTO>> getActions(@PathVariable("id") Long id) {
        List<ActionDTO> actions = actionService.findActions(id);
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }
}
