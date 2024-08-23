import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

// Exceção para valores negativos
class ValorNegativoException extends Exception {
    public ValorNegativoException(String mensagem) {
        super(mensagem);
    }
}

// Exceção para limite insuficiente
class SemLimiteException extends Exception {
    public SemLimiteException(String mensagem) {
        super(mensagem);
    }
}

interface ITaxas {
    double calculaTaxas();
}

abstract class Operacao implements Comparable<Operacao> {
    private Date data;
    private char tipo;
    private float valor;

    public Date getData() {
        return this.data;
    }

    public char getTipo() {
        return this.tipo;
    }

    public void setTipo(char tipo) {
        if (tipo == 'd' || tipo == 's') {
            this.tipo = tipo;
        }
    }

    public float getValor() {
        return this.valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public Operacao(char tipo, float valor) {
        this.tipo = tipo;
        this.valor = valor;
        data = new Date();
    }

    void extrato() {
        System.out.println(getData() + " " + getTipo() + " " + getValor());
    }

    public String toString() {
        return "Operacao{" +
                "data=" + data +
                ", tipo=" + tipo +
                ", valor=" + valor +
                '}';
    }

    public int compareTo(Operacao o) {
        return Character.compare(this.tipo, o.tipo);
    }
}

class Saca extends Operacao {
    public Saca(float valor) {
        super('S', valor);
    }
}

class Deposita extends Operacao {
    public Deposita(float valor) {
        super('D', valor);
    }
}

abstract class Cliente {
    private String nome;
    String endereco;
    Date dataCliente;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public abstract boolean autenticar(String chave);
}

class ClientePessoaFisica extends Cliente {
    String CPF;
    int idade;
    char sexo;

    public String toString() {
        return "ClientePessoaFisica{" +
                "nome='" + getNome() + '\'' +
                ", CPF='" + CPF + '\'' +
                ", endereco='" + endereco + '\'' +
                ", idade=" + idade +
                ", sexo=" + sexo +
                '}';
    }

    public boolean autenticar(String chave) {
        return this.CPF.equals(chave);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientePessoaFisica that = (ClientePessoaFisica) o;
        return Objects.equals(CPF, that.CPF);
    }

    public int hashCode() {
        return Objects.hash(CPF);
    }
}

class ClientePessoaJuridica extends Cliente {
    String CNPJ;
    int numFuncionarios;
    String setor;

    public String toString() {
        return "ClientePessoaJuridica{" +
                "nome='" + getNome() + '\'' +
                ", CNPJ='" + CNPJ + '\'' +
                ", endereco='" + endereco + '\'' +
                ", numFuncionarios=" + numFuncionarios +
                ", setor='" + setor + '\'' +
                '}';
    }

    public boolean autenticar(String chave) {
        return this.CNPJ.equals(chave);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientePessoaJuridica that = (ClientePessoaJuridica) o;
        return Objects.equals(CNPJ, that.CNPJ);
    }

    public int hashCode() {
        return Objects.hash(CNPJ);
    }
}

abstract class Conta {
    List<Operacao> operacoes = new ArrayList<>();
    Cliente cliente;
    private int numero;
    private float saldo_atual = 0;
    protected float limite;

    public int getNumero() {
        return this.numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public float getSaldo() {
        return this.saldo_atual;
    }

    public float getLimite() {
        return this.limite;
    }

    public abstract void setLimite(float limite) throws IllegalArgumentException;

    void saca(float quantidade) throws ValorNegativoException, SemLimiteException {
        if (quantidade < 0) {
            throw new ValorNegativoException("O valor para saque não pode ser negativo.");
        }
        if (saldo_atual - quantidade < -limite) {
            throw new SemLimiteException("Limite insuficiente para saque.");
        }
        saldo_atual -= quantidade;
        operacoes.add(new Saca(quantidade));
        System.out.println("Saque realizado com sucesso, no valor de BRL " + quantidade);
    }

    void depositar(float quantidade) throws ValorNegativoException {
        if (quantidade < 0) {
            throw new ValorNegativoException("O valor para depósito não pode ser negativo.");
        }
        float nv_saldo = saldo_atual + quantidade;
        if (nv_saldo > limite) {
            System.out.println("Limite estourado!!!");
        } else {
            saldo_atual = nv_saldo;
            operacoes.add(new Deposita(quantidade));
            System.out.println("Deposito realizado com sucesso, no valor de BRL " + quantidade);
        }
    }

    void imprimirExtrato(int flag) {
        List<Operacao> extrato = new ArrayList<>(operacoes);
        if (flag == 1) {
            Collections.sort(extrato);
        }

        for (Operacao operacao : extrato) {
            operacao.extrato();
        }
    }

    void imprimirExtratoTaxas() {
        double totalTaxas = 0.0;
        System.out.println("Extrato de Taxas:");
        for (Operacao operacao : operacoes) {
            if (operacao instanceof ITaxas) {
                ITaxas taxa = (ITaxas) operacao;
                double valorTaxa = taxa.calculaTaxas();
                totalTaxas += valorTaxa;
                System.out.println("Taxa: " + valorTaxa);
            }
        }
        System.out.println("Total de Taxas: " + totalTaxas);
    }

    public String toString() {
        return "Conta{" +
                "numero=" + numero +
                ", saldo_atual=" + saldo_atual +
                ", limite=" + limite +
                ", cliente=" + cliente +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return numero == conta.numero;
    }

    public int hashCode() {
        return Objects.hash(numero);
    }
}

class ContaCorrente extends Conta implements ITaxas {
    public void setLimite(float limite) throws IllegalArgumentException {
        if (limite < -100) {
            throw new IllegalArgumentException("O limite mínimo para Conta Corrente é -100.");
        }
        this.limite = limite;
    }

    public double calculaTaxas() {
        if (cliente instanceof ClientePessoaFisica) {
            return 10.0;
        } else if (cliente instanceof ClientePessoaJuridica) {
            return 20.0;
        }
        return 0.0;
    }
}

class ContaPoupanca extends Conta implements ITaxas {
    public void setLimite(float limite) throws IllegalArgumentException {
        if (limite < 100 || limite > 1000) {
            throw new IllegalArgumentException("O limite para Conta Poupanca deve estar entre 100 e 1000.");
        }
        this.limite = limite;
    }

    public double calculaTaxas() {
        return 0.0;
    }
}

class ContaUniversitaria extends Conta implements ITaxas {
    public void setLimite(float limite) throws IllegalArgumentException {
        if (limite < 0 || limite > 500) {
            throw new IllegalArgumentException("O limite para Conta Universitaria deve estar entre 0 e 500.");
        }
        this.limite = limite;
    }

    public double calculaTaxas() {
        return 0.0;
    }
}

class OperacaoSaque extends Operacao implements ITaxas {
    public OperacaoSaque(float valor) {
        super('S', valor);
    }

    public double calculaTaxas() {
        return 0.05;
    }
}

class BMG {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Conta conta = null;
        Cliente cliente = null;
        ClientePessoaFisica pf = new ClientePessoaFisica();
        ClientePessoaJuridica pj = new ClientePessoaJuridica();

        int x;
        int y;
        int z = 0;
        float quantidade;

        System.out.println("-----BANCO BMG-----");
        System.out.println("Vamos criar sua conta!");
        System.out.println("Identifique-se:");
        System.out.println("[1] - Pessoa Fisica");
        System.out.println("[2] - Pessoa Juridica");
        System.out.print("Insira um valor: ");
        y = sc.nextInt();

        switch (y) {
            case 1:
                z = 1;
                System.out.print("Insira o numero da conta: ");
                conta = new ContaCorrente();
                conta.setNumero(sc.nextInt());
                System.out.print("Insira seu limite: ");
                conta.setLimite(sc.nextFloat());
                sc.nextLine();
                System.out.print("Insira seu nome: ");
                pf.setNome(sc.nextLine());
                System.out.print("Insira seu CPF: ");
                pf.CPF = sc.nextLine();
                System.out.print("Insira seu endereco: ");
                pf.endereco = sc.nextLine();
                System.out.print("Insira sua idade: ");
                pf.idade = sc.nextInt();
                System.out.print("Insira seu sexo: ");
                pf.sexo = sc.next().charAt(0);
                conta.cliente = pf;
                break;
            case 2:
                z = 2;
                System.out.print("Insira o numero da conta: ");
                conta = new ContaCorrente();
                conta.setNumero(sc.nextInt());
                System.out.print("Insira seu limite: ");
                conta.setLimite(sc.nextFloat());
                sc.nextLine();
                System.out.print("Insira seu nome: ");
                pj.setNome(sc.nextLine());
                System.out.print("Insira seu CNPJ: ");
                pj.CNPJ = sc.nextLine();
                System.out.print("Insira seu endereco: ");
                pj.endereco = sc.nextLine();
                System.out.print("Insira o numero de funcionarios: ");
                pj.numFuncionarios = sc.nextInt();
                System.out.print("Insira o setor: ");
                pj.setor = sc.next();
                conta.cliente = pj;
                break;
            default:
                System.out.println("Opcao invalida");
        }

        do {
            System.out.println();
            System.out.println("[1] - Sacar");
            System.out.println("[2] - Depositar");
            System.out.println("[3] - Ver Saldo");
            System.out.println("[4] - Imprimir Extrato");
            System.out.println("[5] - Imprimir Extrato Ordenado");
            System.out.println("[6] - Imprimir Extrato de Taxas");
            System.out.println("[0] - Sair");
            System.out.print("Escolha sua opcao: ");
            x = sc.nextInt();

            try {
                switch (x) {
                    case 1:
                        System.out.print("Insira a quantidade: ");
                        quantidade = sc.nextFloat();
                        conta.saca(quantidade);
                        break;
                    case 2:
                        System.out.print("Insira a quantidade: ");
                        quantidade = sc.nextFloat();
                        conta.depositar(quantidade);
                        break;
                    case 3:
                        System.out.println("Seu saldo e: " + conta.getSaldo());
                        break;
                    case 4:
                        conta.imprimirExtrato(0);
                        break;
                    case 5:
                        conta.imprimirExtrato(1);
                        break;
                    case 6:
                        conta.imprimirExtratoTaxas();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opcao invalida");
                }
            } catch (ValorNegativoException | SemLimiteException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (x != 0);

        sc.close();
    }
}
