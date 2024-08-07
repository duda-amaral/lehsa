package com.gerenciadorlehsa.controller;

import com.gerenciadorlehsa.service.TransacaoEntityConverterService;
import com.gerenciadorlehsa.controller.interfaces.AgendamentoController;
import com.gerenciadorlehsa.controller.interfaces.OperacoesCRUDController;
import com.gerenciadorlehsa.dto.AgendamentoDTO;
import com.gerenciadorlehsa.dto.AgendamentoDTORes;
import com.gerenciadorlehsa.entity.Agendamento;
import com.gerenciadorlehsa.repository.AgendamentoRepository;
import com.gerenciadorlehsa.service.MapaTransacaoItemService;
import com.gerenciadorlehsa.service.TransacaoService;
import com.gerenciadorlehsa.service.interfaces.AgendamentoService;
import com.gerenciadorlehsa.service.interfaces.OperacoesCRUDService;
import com.gerenciadorlehsa.util.ConversorEntidadeDTOUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.UUID;
import static com.gerenciadorlehsa.util.ConstantesRequisicaoUtil.*;
import static com.gerenciadorlehsa.util.ConstantesTopicosUtil.AGENDAMENTO_CONTROLLER;
import static com.gerenciadorlehsa.util.ConstrutorRespostaJsonUtil.construirRespostaJSON;
import static com.gerenciadorlehsa.util.ConversorEntidadeDTOUtil.converterParaDtoRes;
import static com.gerenciadorlehsa.util.DataHoraUtil.converterDataHora;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j(topic = AGENDAMENTO_CONTROLLER)
@RestController
@Validated
@RequestMapping(ENDPOINT_AGENDAMENTO)
@AllArgsConstructor
@Tag(name = "Agendamento", description = "APIs relacionadas a operações de agendamento")
public class AgendamentoControllerImpl implements OperacoesCRUDController<AgendamentoDTO, AgendamentoDTORes>, AgendamentoController {

    private final TransacaoService<Agendamento, AgendamentoRepository> transacaoService;
    private final OperacoesCRUDService<Agendamento> operacoesCRUDService;
    private final AgendamentoService agendamentoService;
    private final TransacaoEntityConverterService<Agendamento,AgendamentoDTO> agendamentoEntityConverterService;
    private final MapaTransacaoItemService<Agendamento> mapaTransacaoItemService;


    @Override
    @Operation(summary = "Encontrar agendamento por ID", description = "Retorna um agendamento específico pelo ID fornecido")
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoDTORes> encontrarPorId (@PathVariable  UUID id) {
        log.info(">>> encontrarPorId: recebendo requisição para encontrar agendamento por id");
        Agendamento agendamento = operacoesCRUDService.encontrarPorId(id);
        return ResponseEntity.ok().body(converterParaDtoRes (agendamento));
    }


    @Override
    @Operation(summary = "Criar novo agendamento", description = "Cria um novo agendamento com os detalhes fornecidos")
    @PostMapping
    public ResponseEntity<Map<String, Object>> criar (@Valid @RequestBody AgendamentoDTO agendamentoDTO) {
        log.info(">>> Criar: recebendo requisição para criar agendamento");

        Agendamento agendamento = agendamentoEntityConverterService.convertToEntity(agendamentoDTO);

        agendamento.setId (null);
        mapaTransacaoItemService.validarMapa (agendamento);

        Agendamento agendamentoCriado = operacoesCRUDService.criar(agendamento);

        return ResponseEntity.created (URI.create("/agendamento/" + agendamentoCriado.getId())).body (construirRespostaJSON(CHAVES_AGENDAMENTO_CONTROLLER, asList(CREATED.value(), MSG_AGENDAMENTO_CRIADO, agendamentoCriado.getId())));
    }

    @Override
    @Operation(summary = "Atualizar agendamento", description = "Atualiza um agendamento existente com os detalhes fornecidos")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar (@PathVariable UUID id,
                                                          @Valid @RequestBody AgendamentoDTO obj) {
        log.info(">>> atualizar: recebendo requisição para atualizar agendamento");
        Agendamento agendamento = agendamentoEntityConverterService.convertToEntity (obj);

        agendamento.setId (id);
        mapaTransacaoItemService.validarMapa (id, agendamento);

        Agendamento agendamentoAtt = operacoesCRUDService.atualizar(agendamento);

        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_AGENDAMENTO_CONTROLLER, asList(OK.value(), MSG_AGENDAMENTO_ATUALIZADO, agendamentoAtt.getId())));
    }

    @Override
    @Operation(summary = "Deletar agendamento", description = "Deleta um agendamento específico pelo ID fornecido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar (@PathVariable UUID id) {
        log.info(">>> deletar: recebendo requisição para deletar agendamento");

        operacoesCRUDService.deletar(id);
        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_AGENDAMENTO_CONTROLLER, asList(OK.value(), MSG_AGENDAMENTO_DELETADO, id)));
    }

    @Override
    @Operation(summary = "Listar todos os agendamentos", description = "Lista todos os agendamentos")
    @GetMapping
    public ResponseEntity<List<AgendamentoDTORes>> listarTodos () {
        log.info(">>> listarTodos: recebendo requisição para listar todos agendamentos");
        List<Agendamento> agendamentos = this.operacoesCRUDService.listarTodos();

        return ResponseEntity.ok().body(agendamentos.stream().map(ConversorEntidadeDTOUtil::converterParaDtoRes).toList());
    }

    @Override
    @Operation(summary = "Atualizar status do agendamento", description = "Atualiza o status de um agendamento específico pelo ID fornecido")
    @PatchMapping("/{id}/{status}")
    public ResponseEntity<Map<String, Object>> atualizarStatus (@PathVariable UUID id,
                                                                @PathVariable String status) {
        log.info(">>> atualizarStatus: recebendo requisição para atualizar status do agendamento");

        transacaoService.atualizarStatus(status, id);

        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_AGENDAMENTO_CONTROLLER, asList(OK.value(), MSG_AGENDAMENTO_ATUALIZADO, id)));
    }

    @Override
    @Operation(summary = "Atualizar técnico do agendamento", description = "Atualiza o técnico responsável por um agendamento específico pelo ID fornecido")
    @PatchMapping("/{id}/tecnico/{email}")
    public ResponseEntity<Map<String, Object>> atualizarTecnico(@PathVariable UUID id,
                                                                @PathVariable String email) {
        log.info(">>> atualizarTecnico: recebendo requisição para atualizar tecnico do agendamento");

        agendamentoService.atualizarTecnico(email, id);

        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_AGENDAMENTO_CONTROLLER, asList(OK.value(), MSG_AGENDAMENTO_ATUALIZADO, id)));
    }

    @Override
    @Operation(summary = "Listar datas ocupadas", description = "Lista as datas ocupadas dos agendamentos")
    @GetMapping("/datasOcupadas")
    public ResponseEntity<List<String[]>> listarDatasOcupadas () {
        log.info(">>> listarDatasOcupadas: recebendo requisição para listar datas ocupadas de agendamento");
        List<Object[]> datas = this.agendamentoService.listarDatasOcupadas();
        List<String[]> datasFormatadas = new ArrayList<>(datas.size());

        for (Object[] data : datas) {
            LocalDateTime dataHoraInicio = (LocalDateTime) data[0];
            LocalDateTime dataHoraFim = (LocalDateTime) data[1];
            String[] arrayTemp = new String[2];
            arrayTemp[0] = converterDataHora(dataHoraInicio);
            arrayTemp[1] = converterDataHora(dataHoraFim);
            datasFormatadas.add(arrayTemp);
        }
        return ResponseEntity.ok().body(datasFormatadas);
    }


    @Override
    @Operation(summary = "Confirmar agendamento pelo professor", description = "Confirma um agendamento por parte do professor")
    @PatchMapping("/professor-confirma")
    public ResponseEntity<Map<String, Object>> confirmarAgendamento(@RequestParam("id") UUID id) {
        log.info (" >>> Confirmando o agendamento por parte do professor");
        agendamentoService.professorConfirmaAgendamento(id);

        return ResponseEntity.ok().body(construirRespostaJSON(CHAVES_AGENDAMENTO_CONTROLLER, asList(OK.value(), MSG_AGENDAMENTO_PROFESSOR_CONFIRMA, id)));
    }

}
