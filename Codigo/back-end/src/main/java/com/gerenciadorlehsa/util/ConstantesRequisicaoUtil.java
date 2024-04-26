package com.gerenciadorlehsa.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Contém as constantes relacionadas às requisições
 */
@UtilityClass
public class ConstantesRequisicaoUtil {

    public static final String ENDPOINT_USUARIO = "/usuario";

    public static final String ENDPOINT_AGENDAMENTO = "/agendamento";

    public static final String ENDPOINT_ITEM = "/item";

    public static final String[] CAMINHOS_PUBLICOS = {"/", "/actuator/health", "/usuario/verificar-token"};

    public static final String[] CAMINHOS_PUBLICOS_POST = {"/usuario", "/login"};

/*    public static final List<String> METODOS_PERMITIDOS_CORS = new ArrayList<>(asList("GET", "POST", "PUT", "PATCH", "HEAD", "OPTIONS"));*/

    //public static final String CONFIGURACAO_CORS = "/**";

    public static final String HEADER_AUTORIZACAO = "Authorization";

    public static final String VALOR_HEADER_AUTORIZACAO = "Bearer %s";

    public static final String TIPO_TOKEN = "Bearer";

    public static final String CONTENT_TYPE = "application/json";

    public static final String CHARACTER_ENCODING = "UTF-8";

    public static final String CORPO_RESPOSTA_REQUISICAO = "{" +
            "\n\"token\": \"" + "%s\"" +

            "\n}";

    public static final String[] PROPRIEDADES_IGNORADAS = new String[]{"id", "password", "perfilUsuario","nota"};


    public static final List<String> CHAVES_USUARIO_CONTROLLER = new ArrayList<>(asList("status", "mensagem", "id_usuario"));

    public static final String MSG_USUARIO_CRIADO = "usuario criado com sucesso";

    public static final String MSG_AGENDAMENTO_CRIADO = "agendamento criado com sucesso";

    public static final String MSG_AGENDAMENTO_ATUALIZADO = "agendamento atualizado com sucesso";

    public static final String MSG_AGENDAMENTO_DELETADO = "agendamento deletado com sucesso";

    public static final String MSG_USUARIO_ATUALIZADO = "usuario atualizado com sucesso";

    public static final String MSG_PERFIL_ATUALIZADO = "perfil do usuário atualizado com sucesso";

    public static final String MSG_USUARIO_DELETADO = "usuario deletado com sucesso";

    public static final String MSG_USUARIO_SENHA = "senha atualizada com sucesso";
}
