
# Memori

<p align="center">
  <img src="https://github.com/larissatoyohashi/memori/blob/main/memori/public/imgs/Banner.png?raw=true" width="90%">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white" alt="Android Studio">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/node.js-6DA55F?style=for-the-badge&logo=node.js&logoColor=white" alt="Node.js">
  <img src="https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB">
</p>

Aplicativo Android desenvolvido em Kotlin que leva o patrimônio histórico do Vale do Ribeira para dentro de um jogo. O usuário explora o mapa, aponta a câmera para o marcador do Conjunto Monumental KKKK, visualiza um modelo 3D em realidade aumentada, resolve um quebra-cabeça e disputa o ranking com outros jogadores.

O Memori faz parte de um ecossistema composto pelo aplicativo Android, pelo CMS de Administração e por uma API em Node.js conectada ao MongoDB Atlas.

- **CMS e API:** https://github.com/AnandaMazine/memori-pi-1-2026

---

## Funcionalidades

- Cadastro e login de usuários com autenticação JWT.
- Mapa interativo com OpenStreetMap e localização do usuário.
- Yumi, personagem guia que apresenta a história por meio de diálogos.
- Realidade aumentada com reconhecimento de marcador e exibição de modelo 3D.
- Quebra-cabeça interativo com cronômetro.
- Ranking com pontuações dos jogadores armazenadas no banco de dados.
- Perfil de usuário com avatar e conquistas.

---

## Arquitetura

```text
App Android (Kotlin)  ──┐
                        ├──► API Node.js (Render) ──► MongoDB Atlas
CMS Web (Admin)       ──┘
```

O aplicativo Android e o CMS utilizam a mesma API para realizar operações de cadastro, login, gerenciamento de conteúdo, pontuação e ranking. O acesso ao banco de dados é feito exclusivamente pelo backend.

---

## Tecnologias

| Camada | Tecnologia | Descrição |
|:---|:---|:---|
| Desenvolvimento Android | Kotlin | Linguagem utilizada no desenvolvimento do aplicativo. |
| Interface | Jetpack Compose | Construção das interfaces visuais do aplicativo. |
| Design | Material 3 | Componentes e padrões visuais da interface. |
| Navegação | Navigation Compose | Gerenciamento da navegação entre telas. |
| Comunicação com a API | Retrofit | Realização de requisições HTTP para o backend. |
| Conversão de dados | Gson | Conversão de JSON em objetos Kotlin e vice-versa. |
| Mapas | OSMDroid | Exibição e interação com mapas. |
| Dados geográficos | OpenStreetMap | Fonte dos dados cartográficos utilizados no mapa. |
| Realidade aumentada | ARCore | Suporte à experiência de realidade aumentada. |
| Renderização 3D | SceneView | Exibição de modelos tridimensionais. |
| Backend | Node.js | Ambiente de execução da API. |
| Servidor HTTP | Express | Gerenciamento das rotas e requisições da API. |
| ODM | Mongoose | Modelagem e comunicação com o MongoDB. |
| Autenticação | JWT | Autenticação e gerenciamento de tokens de acesso. |
| Banco de dados | MongoDB Atlas | Armazenamento dos usuários, pontuações e demais dados. |
| Hospedagem | Render | Hospedagem da API na internet. |
| IDE | Android Studio | Ambiente de desenvolvimento do aplicativo Android. |

---

## Executando o projeto

O aplicativo já está configurado para utilizar a API hospedada no Render. Portanto, **não é necessário instalar o Node.js, configurar o MongoDB ou iniciar um servidor local para utilizar o Memori**.

Basta clonar o projeto, abrir no Android Studio e executar o aplicativo.

### Pré-requisitos

- Android Studio (versão recente).
- Celular Android 7.0 ou superior compatível com ARCore.
- Cabo USB para executar o aplicativo em um dispositivo físico.
- Conexão com a internet.

Links úteis:

- [Download do Android Studio](https://developer.android.com/studio)
- [Lista de dispositivos compatíveis com ARCore](https://developers.google.com/ar/devices)

### 1. Clonar o repositório

```bash
git clone https://github.com/samsesz/Memori-PI.git
```

Abra a pasta clonada no Android Studio e aguarde a sincronização do Gradle.

### 2. Executar o aplicativo

1. Abra o projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Conecte um celular Android ao computador ou selecione um emulador compatível.
4. Clique em **Run ▶** para iniciar o aplicativo.

### 3. Fazer login ou criar uma conta

Ao abrir o Memori:

1. Caso já possua uma conta, informe seu e-mail e senha na tela de login.
2. Caso ainda não tenha cadastro, acesse a tela de cadastro e preencha as informações solicitadas.
3. Seus dados serão enviados automaticamente para a API hospedada no Render e armazenados no MongoDB Atlas.
4. Após o cadastro, faça login para acessar as funcionalidades do aplicativo.

**Não é necessário realizar cadastro manual no banco de dados ou configurar um servidor.** O cadastro é realizado pela API, que processa e armazena as informações automaticamente.

### 4. Permissões do aplicativo

Ao abrir o aplicativo, permita o acesso aos seguintes recursos:

- **Câmera:** necessária para utilizar a realidade aumentada.
- **Localização:** necessária para exibir a posição do usuário no mapa.

---

## Como jogar

1. Crie uma conta ou entre com seu login.
2. No mapa, abra a trilha **Lanternas Brilhantes** e toque em **Jogar**.
3. A personagem Yumi apresentará a história por meio de diálogos. Toque na tela para avançar.
4. Vá até o Conjunto Monumental KKKK e aponte a câmera para o marcador (QR Code) para visualizar o modelo 3D.
5. Monte o quebra-cabeça tocando nas peças ao lado do espaço vazio.
6. Consulte sua posição no ranking e compare sua pontuação com a dos outros jogadores.

> **Testando sem estar no local:** abra a imagem `app/src/main/assets/marker.jpg` em outra tela e aponte a câmera do celular para ela. O tamanho ideal do marcador é de aproximadamente 20 cm de largura.

---

## Estrutura do projeto

```text
app/src/main/
├── assets/
│   ├── marker.jpg
│   └── model.glb
│
├── java/com/memori/app/
│   ├── MainActivity.kt
│   ├── data/
│   │   └── remote/         # Cliente HTTP (Retrofit) e modelos da API
│   │
│   └── ui/
│       ├── navigation/     # Rotas do aplicativo
│       ├── screens/
│       │   ├── auth/       # Splash, login e cadastro
│       │   ├── home/       # Mapa e menu principal
│       │   ├── profile/    # Perfil e conquistas
│       │   └── trail/      # História, AR, quebra-cabeça e ranking
│       │
│       └── theme/          # Cores e tipografia (Pangolin)
│
└── res/                    # Imagens, fontes e recursos
```

---

## Solução de problemas

| Problema | Solução |
|:---|:---|
| Não consigo fazer login | Confira se o e-mail e a senha estão corretos. Caso ainda não possua conta, realize o cadastro. |
| Não consigo acessar a API | Verifique sua conexão com a internet e tente novamente. A API está hospedada no Render e pode levar alguns instantes para responder após um período sem requisições. |
| O aplicativo não consegue salvar os dados | Verifique se a conexão com a internet está funcionando e tente novamente. |
| O aplicativo solicita uma versão mais recente do Google Play Services para RA | Instale ou atualize o Google Play Services for AR pela Play Store. |
| A câmera não abre | Conceda a permissão de câmera em Configurações → Apps → Memori → Permissões. |
| O Gradle não sincroniza | Verifique sua conexão com a internet e utilize File → Sync Project with Gradle Files no Android Studio. |

---

## Observações

- O aplicativo utiliza a API hospedada no Render e o banco de dados MongoDB Atlas.
- Os dados de usuários, pontuações e rankings são armazenados no banco de dados compartilhado do projeto.
- Não é necessário configurar arquivos `.env` para executar o aplicativo Android.
- As variáveis de ambiente e configurações de segurança são responsabilidade do backend.
- É necessária conexão com a internet para utilizar as funcionalidades que dependem da API.
- Compatível com Android 7.0 (API 24) ou superior.
- Consulte o arquivo `LICENSE` para conhecer os termos de uso.
