package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.PacienteDao;
import br.com.fiap.hc.dto.paciente.LoginPacienteDto;
import br.com.fiap.hc.model.request.LoginRequest;
import br.com.fiap.hc.model.response.LoginResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    private PacienteDao pacienteDao;

    @Inject
    private ModelMapper modelMapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginPacienteDto dto) throws SQLException {

        LoginRequest login = modelMapper.map(dto, LoginRequest.class);
        LoginResponse paciente = pacienteDao.login(login.getCpf(), login.getEmail());

        if (paciente == null) {
            return Response.status(401).entity("CPF ou Email incorretos").build();
        }
        return Response.ok(paciente).build();
    }
}
