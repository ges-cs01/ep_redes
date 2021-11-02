# ep_redes

Programa que realiza via rede em um modelo cliente-servidor a troca de arquivos entre usuários. 

## Instalação

Faça o download do arquivo .zip ou clone localmente o repositório.

    git clone https://github.com/ges-cs01/ep_redes.git

## Compilação
Vá até o diretório /servidor e compile com o comando:

    javac *.java
    
Em seguida vá até o diretório /cliente e compile com o comando:
    
    javac Client.java
  
## Uso
Execute em um terminal o servidor:

    java Server
    
Em outro terminal ou máquina execute o cliente:

    java Client


## Troubleshooting
  - java.awt.HeadlessException: No X11 DISPLAY variable was set, but this program performed an operation which requires it
      #### Solução
      
      Configurar a váriavel $DISPLAY com o comando:
  
        export DISPLAY=:0.0
  
 





