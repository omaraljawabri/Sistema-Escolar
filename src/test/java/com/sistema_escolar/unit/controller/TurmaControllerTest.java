package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.TurmaController;
import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CodeRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.dtos.request.TurmaRequestDTO;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.*;
import com.sistema_escolar.services.TurmaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.sistema_escolar.utils.EntityUtils.mockAuthentication;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TurmaControllerTest {

    @InjectMocks
    private TurmaController turmaController;

    @Mock
    private TurmaService turmaService;

    @BeforeEach
    void setup(){
        doNothing().when(turmaService).criarTurma(ArgumentMatchers.any(CreateTurmaRequestDTO.class));
        doNothing().when(turmaService).addEstudante(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        doNothing().when(turmaService).addProfessor(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        when(turmaService.gerarCodigo(ArgumentMatchers.any(TurmaRequestDTO.class)))
                .thenReturn(new CodeResponseDTO("9514367@#"));
        when(turmaService.gerarCodigo(ArgumentMatchers.any(Usuario.class)))
                .thenReturn(new CodeResponseDTO("64075307$"));
        doNothing().when(turmaService).entrarTurma(ArgumentMatchers.any(CodeRequestDTO.class), ArgumentMatchers.any(Usuario.class));
    }

    @Test
    @DisplayName("criarTurma deve cadastrar uma turma no sistema quando a operação for bem sucedida")
    void criarTurma_CadastraUmaTurmaNoSistema_QuandoBemSucedido() {
        assertThatCode(() -> turmaController.criarTurma(new CreateTurmaRequestDTO("Turma A", 1L)))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = turmaController.criarTurma(new CreateTurmaRequestDTO("Turma A", 1L));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("criarTurma deve lançar uma EntityNotFoundException quando o id da disciplina passado não existir")
    void criarTurma_LancaEntityNotFoundException_QuandoDisciplinaIdNaoExistir(){
        doThrow(new EntityNotFoundException("Disciplina passada não existe"))
                .when(turmaService).criarTurma(ArgumentMatchers.any(CreateTurmaRequestDTO.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaController.criarTurma(new CreateTurmaRequestDTO("Turma A", 2L)))
                .withMessage("Disciplina passada não existe");
    }

    @Test
    @DisplayName("criarTurma deve lançar uma EntityAlreadyExistsException quando o nome da turma já existir no banco de dados")
    void criarTurma_LancaEntityAlreadyExistsException_QuandoNomeDaTurmaJaExistir(){
        doThrow(new EntityAlreadyExistsException("A turma que está sendo criada já existe"))
                .when(turmaService).criarTurma(ArgumentMatchers.any(CreateTurmaRequestDTO.class));
        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> turmaController.criarTurma(new CreateTurmaRequestDTO("Turma A", 1L)))
                .withMessage("A turma que está sendo criada já existe");
    }

    @Test
    @DisplayName("addEstudante deve registrar um estudante em uma turma quando a operação for bem sucedida")
    void addEstudante_RegistraUmEstudanteEmUmaTurma_QuandoBemSucedido() {
        assertThatCode(() -> turmaController.addEstudante(new AddTurmaRequestDTO("ciclano@gmail.com", 1L)))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = turmaController.addEstudante(new AddTurmaRequestDTO("ciclano@gmail.com", 1L));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("addEstudante deve lançar uma UserNotFoundException quando e-mail do estudante passado não existir")
    void addEstudante_LancaUserNotFoundException_QuandoEmailDoEstudanteNaoExistir(){
        doThrow(new UserNotFoundException("Email do estudante que deseja adicionar não existe"))
                .when(turmaService).addEstudante(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> turmaController.addEstudante(new AddTurmaRequestDTO("fulano@gmail.com", 1L)))
                .withMessage("Email do estudante que deseja adicionar não existe");
    }

    @Test
    @DisplayName("addEstudante deve lançar uma EntityNotFoundException quando id da turma passado não existir")
    void addEstudante_LancaEntityNotFoundException_QuandoTurmaIdNaoExistir(){
        doThrow(new EntityNotFoundException("Turma selecionada não existe"))
                .when(turmaService).addEstudante(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaController.addEstudante(new AddTurmaRequestDTO("ciclano@gmail.com", 2L)))
                .withMessage("Turma selecionada não existe");
    }

    @Test
    @DisplayName("addEstudante deve lançar uma UserAlreadyBelongsToAnEntityException quando o estudante já estiver cadastro na turma passada")
    void addEstudante_LancaUserAlreadyBelongsToAnEntityException_QuandoEstudanteJaEstiverCadastradoNaTurma(){
        doThrow(new UserAlreadyBelongsToAnEntityException("Estudante já está cadastrado nesta turma!"))
                .when(turmaService).addEstudante(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaController.addEstudante(new AddTurmaRequestDTO(("ciclano@gmail.com"), 1L)))
                .withMessage("Estudante já está cadastrado nesta turma!");
    }

    @Test
    @DisplayName("addProfessor deve registrar um professor em uma turma quando a operação for bem sucedida")
    void addProfessor_RegistraUmProfessorEmUmaTurma_QuandoBemSucedido() {
        assertThatCode(() -> turmaController.addProfessor(new AddTurmaRequestDTO("professor@gmail.com", 1L)))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = turmaController.addProfessor(new AddTurmaRequestDTO("professor@gmail.com", 1L));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("addProfessor deve lançar uma UserNotFoundException quando o e-mail do professor passado não existir")
    void addProfessor_LancaUserNotFoundException_QuandoEmailDoProfessorNaoExistir(){
        doThrow(new UserNotFoundException("Email do professor que deseja adicionar não existe"))
                .when(turmaService).addProfessor(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> turmaController.addProfessor(new AddTurmaRequestDTO("ciclano@gmail.com", 1L)))
                .withMessage("Email do professor que deseja adicionar não existe");
    }

    @Test
    @DisplayName("addProfessor deve lançar uma EntityNotFoundException quando o id da turma passado não existir")
    void addProfessor_LancaEntityNotFoundException_QuandoTurmaIdNaoExistir(){
        doThrow(new EntityNotFoundException("Turma selecionada não existe"))
                .when(turmaService).addProfessor(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaController.addProfessor(new AddTurmaRequestDTO("professor@gmail.com", 2L)))
                .withMessage("Turma selecionada não existe");
    }

    @Test
    @DisplayName("addProfessor deve lançar uma UserAlreadyBelongsToAnEntityException quando professor já estiver cadastro na turma passada")
    void addProfessor_LancaUserAlreadyBelongsToAnEntityException_QuandoProfessorJaEstiverCadastradoNaTurma(){
        doThrow(new UserAlreadyBelongsToAnEntityException("Professor já está cadastrado nesta turma!"))
                .when(turmaService).addProfessor(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaController.addProfessor(new AddTurmaRequestDTO("professor@gmail.com", 1L)))
                .withMessage("Professor já está cadastrado nesta turma!");
    }

    @Test
    @DisplayName("addProfessor deve lançar uma UserAlreadyBelongsToAnEntityException quando professor já fizer parte de uma outra disciplina")
    void addProfessor_LancaUserAlreadyBelongsToAnEntityException_QuandoProfessorFazParteDeOutraDisciplina(){
        doThrow(new UserAlreadyBelongsToAnEntityException("Professor já está cadastrado em outra disciplina"))
                .when(turmaService).addProfessor(ArgumentMatchers.any(AddTurmaRequestDTO.class));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaController.addProfessor(new AddTurmaRequestDTO("professor@gmail.com", 1L)))
                .withMessage("Professor já está cadastrado em outra disciplina");
    }

    @Test
    @DisplayName("gerarCodigo deve gerar um código de uma turma pelo seu id quando solicitado por um ADMIN")
    void gerarCodigo_GeraCodigoDeUmaTurmaPeloId_QuandoSolicitadoPorUmAdmin() {
        ResponseEntity<CodeResponseDTO> codeResponseDTOResponseEntity = turmaController.gerarCodigo(new TurmaRequestDTO(1L));
        assertThat(codeResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(codeResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(codeResponseDTOResponseEntity.getBody().getCode()).isEqualTo("9514367@#");
    }

    @Test
    @DisplayName("gerarCodigo deve lançar uma EntityNotFoundException quando o id da turma passado não existir e quando for solicitado por um ADMIN")
    void gerarCodigo_LancaEntityNotFoundException_QuandoIdDaTurmaNaoExistirEForSolicitadoPorUmAdmin(){
        doThrow(new EntityNotFoundException("Turma selecionada não existe"))
                .when(turmaService).gerarCodigo(ArgumentMatchers.any(TurmaRequestDTO.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> turmaController.gerarCodigo(new TurmaRequestDTO(2L)))
                .withMessage("Turma selecionada não existe");
    }

    @Test
    @DisplayName("gerarCodigo deve gerar um código da turma do usuário quando solicitado por um PROFESSOR")
    void gerarCodigo_GeraCodigoDaTurmaDoUsuario_QuandoSolicitadoPorUmProfessor(){
        mockAuthentication();
        ResponseEntity<CodeResponseDTO> codeResponseDTOResponseEntity = turmaController.gerarCodigo();
        assertThat(codeResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(codeResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(codeResponseDTOResponseEntity.getBody().getCode()).isEqualTo("64075307$");
    }

    @Test
    @DisplayName("gerarCodigo deve lançar uma UserDoesntBelongException quando o professor não estiver cadastrado no sistema e for solicitado por um PROFESSOR")
    void gerarCodigo_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverCadastradoEForSolicitadoPorUmProfessor(){
        mockAuthentication();
        doThrow(new UserDoesntBelongException("Professor não esta vinculado a nenhuma turma"))
                .when(turmaService).gerarCodigo(ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> turmaController.gerarCodigo())
                .withMessage("Professor não esta vinculado a nenhuma turma");
    }

    @Test
    @DisplayName("entrarTurma deve registrar um Usuario em uma turma quando a operação for bem sucedida")
    void entrarTurma_RegistraUmUsuarioEmUmaTurma_QuandoBemSucedido() {
        mockAuthentication();
        assertThatCode(() -> turmaController.entrarTurma(new CodeRequestDTO("9514367@#")))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = turmaController.entrarTurma(new CodeRequestDTO("9514367@#"));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma InvalidCodeException quando o código da turma passado não existir")
    void entrarTurma_LancaInvalidCodeException_QuandoCodigoDaTurmaNaoExistir(){
        mockAuthentication();
        doThrow(new InvalidCodeException("Código da turma não existe!"))
                .when(turmaService).entrarTurma(ArgumentMatchers.any(CodeRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> turmaController.entrarTurma(new CodeRequestDTO("4603784554")))
                .withMessage("Código da turma não existe!");
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma InvalidCodeException quando o código da turma passado já estiver expirado")
    void entrarTurma_LancaInvalidCodeException_QuandoCodigoDaTurmaEstiverExpirado(){
        mockAuthentication();
        doThrow(new InvalidCodeException("Código da turma está expirado!"))
                .when(turmaService).entrarTurma(ArgumentMatchers.any(CodeRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> turmaController.entrarTurma(new CodeRequestDTO("9514367@#")))
                .withMessage("Código da turma está expirado!");
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma UserAlreadyBelongsToAnEntityException quando usuário for um professor e já estiver vinculado a essa turma")
    void entrarTurma_LancaUserAlreadyBelongsToAnEntityException_QuandoUsuarioForProfessorEJaEstiverVinculadoATurma(){
        mockAuthentication();
        doThrow(new UserAlreadyBelongsToAnEntityException("Professor já está vinculado a essa turma"))
                .when(turmaService).entrarTurma(ArgumentMatchers.any(CodeRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaController.entrarTurma(new CodeRequestDTO("9514367@#")))
                .withMessage("Professor já está vinculado a essa turma");
    }

    @Test
    @DisplayName("entrarTurma deve lançar uma UserAlreadyBelongsToAnEntityException quando usuário for um estudante e já estiver vinculado a uma turma da mesma disciplina")
    void entrarTurma_LancaUserAlreadyBelongsToAnEntityException_QuandoUsuarioForProfessorEJaEstiverVinculadoAUmaTurmaDaMesmaDisciplina(){
        mockAuthentication();
        doThrow(new UserAlreadyBelongsToAnEntityException("Estudante já está vinculado a uma turma desta disciplina!"))
                .when(turmaService).entrarTurma(ArgumentMatchers.any(CodeRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> turmaController.entrarTurma(new CodeRequestDTO("9514367@#")))
                .withMessage("Estudante já está vinculado a uma turma desta disciplina!");
    }
}