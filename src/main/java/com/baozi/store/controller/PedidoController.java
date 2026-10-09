package com.baozi.store.controller;

import com.baozi.store.model.Cliente;
import com.baozi.store.model.Pedido;
import com.baozi.store.model.Produto;
import com.baozi.store.repository.ClienteRepository;
import com.baozi.store.repository.PedidoRepository;
import com.baozi.store.repository.ProdutoRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody Pedido pedido) {
        String erro = preencherClienteEProduto(pedido, pedido);
        if (erro != null) {
            return ResponseEntity.badRequest().body(erro);
        }
        pedido.setId(null);
        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.created(URI.create("/pedidos/" + salvo.getId())).body(salvo);
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody Pedido dados) {
        Optional<Pedido> existente = pedidoRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Pedido pedido = existente.get();
        String erro = preencherClienteEProduto(dados, pedido);
        if (erro != null) {
            return ResponseEntity.badRequest().body(erro);
        }
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Valida os dados recebidos e copia para o pedido o cliente e o produto
    // completos do banco. Retorna a mensagem de erro, ou null se estiver tudo certo.
    private String preencherClienteEProduto(Pedido dados, Pedido pedido) {
        if (dados.getCliente() == null || dados.getCliente().getId() == null) {
            return "Informe o cliente do pedido.";
        }
        if (dados.getProduto() == null || dados.getProduto().getId() == null) {
            return "Informe o produto do pedido.";
        }
        if (dados.getQuantidade() == null || dados.getQuantidade() <= 0) {
            return "A quantidade deve ser maior que zero.";
        }
        Optional<Cliente> cliente = clienteRepository.findById(dados.getCliente().getId());
        if (cliente.isEmpty()) {
            return "Cliente " + dados.getCliente().getId() + " não encontrado.";
        }
        Optional<Produto> produto = produtoRepository.findById(dados.getProduto().getId());
        if (produto.isEmpty()) {
            return "Produto " + dados.getProduto().getId() + " não encontrado.";
        }
        pedido.setCliente(cliente.get());
        pedido.setProduto(produto.get());
        pedido.setQuantidade(dados.getQuantidade());
        return null;
    }
}
