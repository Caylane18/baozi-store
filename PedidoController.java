package com.baozi.store.controller;

import com.baozi.store.model.Pedido;
import com.baozi.store.repository.ClienteRepository;
import com.baozi.store.repository.PedidoRepository;
import com.baozi.store.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // Um pedido só é válido se o cliente e o produto existirem e a quantidade for maior que zero
    private boolean pedidoValido(Pedido pedido) {
        return pedido.getClienteId() != null
                && pedido.getProdutoId() != null
                && pedido.getQuantidade() != null
                && pedido.getQuantidade() > 0
                && clienteRepository.existsById(pedido.getClienteId())
                && produtoRepository.existsById(pedido.getProdutoId());
    }

    // POST - criar
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        if (!pedidoValido(pedido)) {
            return ResponseEntity.badRequest()
                    .body("Pedido inválido: informe clienteId e produtoId existentes e quantidade maior que zero.");
        }
        pedido.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoRepository.save(pedido));
    }

    // GET /{id} - consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - listar todos
    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    // PUT /{id} - atualizar (opcional)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!pedidoValido(pedido)) {
            return ResponseEntity.badRequest()
                    .body("Pedido inválido: informe clienteId e produtoId existentes e quantidade maior que zero.");
        }
        pedido.setId(id);
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    // DELETE /{id} - apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
