package com.puenteblanco.pb.controller.vet;

import com.puenteblanco.pb.dto.request.VetAssignInternRequestDto;
import com.puenteblanco.pb.services.interfaces.VetAssignInternService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/vet/appointments")
public class VetAssignInternController {

    private final VetAssignInternService vetAssignInternService;

    public VetAssignInternController(VetAssignInternService vetAssignInternService) {
        this.vetAssignInternService = vetAssignInternService;
    }

    @PutMapping("/{id}/assign-intern")
    public void assignInternToAppointment(
            @PathVariable("id") Long citaId,
            @Valid @RequestBody VetAssignInternRequestDto request,
            Principal principal
    ) {
        vetAssignInternService.assignInternToAppointment(citaId, request.getInternId(), principal.getName());
    }
}
