package com.gerenciadorlehsa.controller;

import com.gerenciadorlehsa.components.abstracts.TransacaoEntityConverterComp;
import com.gerenciadorlehsa.controller.interfaces.OperacoesCRUDController;
import com.gerenciadorlehsa.dto.EmprestimoDTO;
import com.gerenciadorlehsa.dto.EmprestimoDTORes;
import com.gerenciadorlehsa.entity.Emprestimo;
import com.gerenciadorlehsa.service.MapaTransacaoItemService;
import com.gerenciadorlehsa.service.TransacaoService;
import com.gerenciadorlehsa.service.interfaces.EmprestimoService;
import com.gerenciadorlehsa.service.interfaces.OperacoesCRUDService;
import com.gerenciadorlehsa.util.ConversorEntidadeDTOUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.gerenciadorlehsa.util.ConstantesRequisicaoUtil.*;
import static com.gerenciadorlehsa.util.ConstantesTopicosUtil.EMPRESTIMO_CONTROLLER;
import static com.gerenciadorlehsa.util.ConstrutorRespostaJsonUtil.construirRespostaJSON;
import static com.gerenciadorlehsa.util.ConversorEntidadeDTOUtil.converterParaDtoRes;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = EMPRESTIMO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_EMPRESTIMO)
@AllArgsConstructor
public class EmprestimoControllerImpl implements OperacoesCRUDController<EmprestimoDTO, EmprestimoDTORes> {

    private final TransacaoService<Emprestimo> transacaoService;
    private final OperacoesCRUDService<Emprestimo> operacoesCRUDService;
    private final EmprestimoService emprestimoService;
    private final MapaTransacaoItemService<Emprestimo> mapaTransacaoItemService;
    TransacaoEntityConverterComp<Emprestimo, EmprestimoDTO> emprestimoEntityConverterComp;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoDTORes> encontrarPorId (@PathVariable UUID id) {
        Emprestimo emprestimo = operacoesCRUDService.encontrarPorId(id);
        return ResponseEntity.ok().body(converterParaDtoRes(emprestimo));
    }

    @Override
    @PostMapping
    public ResponseEntity<Map<String, Object>> criar (@Valid @RequestBody EmprestimoDTO obj) {
        Emprestimo emprestimo = emprestimoEntityConverterComp.convertToEntity(obj);
        mapaTransacaoItemService.validarMapaParaCriacao (emprestimo);
        Emprestimo emprestimoCriado = operacoesCRUDService.criar(emprestimo);
        return ResponseEntity.created (URI.create("/emprestimo/" + emprestimoCriado.getId())).body (construirRespostaJSON(CHAVES_EMPRESTIMO_CONTROLLER, asList(CREATED.value(), MSG_EMPRESTIMO_CRIADO, emprestimoCriado.getId())));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar (@PathVariable UUID id,
                                                          @Valid @RequestBody EmprestimoDTO obj) {
        Emprestimo emprestimo = emprestimoEntityConverterComp.convertToEntity(obj);
        mapaTransacaoItemService.validarMapaParaCriacao (emprestimo);
        emprestimo.setId(id);
        Emprestimo emprestimoAtt = operacoesCRUDService.atualizar(emprestimo);
        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_EMPRESTIMO_CONTROLLER, asList(OK.value(), MSG_EMPRESTIMO_ATUALIZADO, emprestimoAtt.getId())));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar (@PathVariable UUID id) {
        operacoesCRUDService.deletar(id);
        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_EMPRESTIMO_CONTROLLER, asList(OK.value(), MSG_EMPRESTIMO_DELETADO, id)));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<EmprestimoDTORes>> listarTodos () {
        List<Emprestimo> emprestimos = operacoesCRUDService.listarTodos();
        return ResponseEntity.ok().body(emprestimos.stream().map(ConversorEntidadeDTOUtil::converterParaDtoRes).toList());
    }
}
