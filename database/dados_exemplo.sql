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
('Cliente Demonstração Dois', 'cliente.dois@example.com', '(11) 90000-0002')
ON DUPLICATE KEY UPDATE email = email;
