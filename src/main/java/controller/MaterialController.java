package controller;

import model.Material.MaterialRequest;
import model.Material.MaterialResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.MaterialService;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    private MaterialService materialService;

    @PostMapping
    public ResponseEntity<MaterialResponse> registerMaterial(@RequestBody MaterialRequest materialRequest) {
        try {
            MaterialResponse materialResponse = materialService.registerMaterial(materialRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(materialResponse);
        } catch (InvalidDataException e) {
            // Se os dados forem inválidos, retornar uma resposta de erro com uma mensagem adequada
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (InsufficientStockException e) {
            // Se houver estoque insuficiente, retornar uma resposta de erro com uma mensagem adequada
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }
}
