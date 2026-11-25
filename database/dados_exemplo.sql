USE cardtopia_db;
INSERT INTO produtos (nome, codigo, categoria, edicao, preco, estoque, foil)
VALUES
('Secret Lair x Dune: Spice', 'DUNE-SPICE-F', 'Secret Lair', 'My Arrakis, My Superdrop', 450.00, 5, 1),
('Secret Lair x Dune: Balance of Power', 'DUNE-BALANCE-N', 'Secret Lair', 'My Arrakis, My Superdrop', 400.00, 2, 0),
('Secret Lair x The Hobbit: Smaug''s Spoils', 'HOBBIT-SMAUG-F', 'Secret Lair', 'The Hobbit', 500.00, 8, 1)
ON DUPLICATE KEY UPDATE codigo = codigo;
INSERT INTO clientes (nome, email, telefone)
VALUES
('Cliente Demonstração Um', 'cliente.um@example.com', '(11) 90000-0001'),
('Cliente Demonstração Dois', 'cliente.dois@example.com', '(11) 90000-0002'),
('Cliente Demonstração Três', 'cliente.tres@example.com', '(11) 90000-0003'),
('Cliente Demonstração Quatro', 'cliente.quatro@example.com', '(11) 90000-0004'),
('Cliente Demonstração Cinco', 'cliente.cinco@example.com', '(11) 90000-0005')
ON DUPLICATE KEY UPDATE email = email;

START TRANSACTION;
INSERT INTO vendas (produto_id, cliente_id, quantidade, preco_unitario, total, status, criado_em)
SELECT p.id, c.id, 1, p.preco, p.preco, 'PENDENTE', NOW()
FROM produtos p
JOIN clientes c ON c.email = 'cliente.um@example.com'
WHERE p.codigo = 'DUNE-SPICE-F'
  AND p.estoque >= 1
  AND NOT EXISTS (
    SELECT 1 FROM vendas v
    WHERE v.produto_id = p.id AND v.cliente_id = c.id AND v.quantidade = 1
  );
SET @pending_order_created = ROW_COUNT();
UPDATE produtos SET estoque = estoque - 1
WHERE codigo = 'DUNE-SPICE-F' AND @pending_order_created = 1;

INSERT INTO vendas (produto_id, cliente_id, quantidade, preco_unitario, total, status, criado_em)
SELECT p.id, c.id, 1, p.preco, p.preco, 'CONFIRMADA', NOW()
FROM produtos p
JOIN clientes c ON c.email = 'cliente.dois@example.com'
WHERE p.codigo = 'DUNE-BALANCE-N'
  AND p.estoque >= 1
  AND NOT EXISTS (
    SELECT 1 FROM vendas v
    WHERE v.produto_id = p.id AND v.cliente_id = c.id AND v.quantidade = 1
  );
SET @confirmed_order_created = ROW_COUNT();
UPDATE produtos SET estoque = estoque - 1
WHERE codigo = 'DUNE-BALANCE-N' AND @confirmed_order_created = 1;

INSERT INTO vendas (produto_id, cliente_id, quantidade, preco_unitario, total, status, criado_em)
SELECT p.id, c.id, 1, p.preco, p.preco, 'CANCELADA', NOW()
FROM produtos p
JOIN clientes c ON c.email = 'cliente.tres@example.com'
WHERE p.codigo = 'HOBBIT-SMAUG-F'
  AND NOT EXISTS (
    SELECT 1 FROM vendas v
    WHERE v.produto_id = p.id AND v.cliente_id = c.id AND v.quantidade = 1
  );
COMMIT;
