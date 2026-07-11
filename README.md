# ImageRenderUtill

**ImageRenderUtill** — это Fabric-утилита (библиотека) для Minecraft **1.21.4**, которая позволяет
выводить произвольные PNG-картинки поверх экрана (HUD) и управлять ими прямо из кода мода.

Это не самостоятельный мод с командами/GUI-настройками — это **инструмент для разработчиков**,
который вы подключаете к своему проекту и вызываете из своего Java-кода. Вся настройка
(позиция, размер, слои, видимость) делается через один класс — `ImageManager`.

## Возможности

1. **Статичная картинка** — вывести любую PNG-картинку на экран с заданной позицией и размером.
2. **Перетаскиваемая картинка** — картинка, которую пользователь может подвинуть мышкой,
   зажав ЛКМ **пока открыт экран чата** (нажата `T` / открыт чат). Новая позиция автоматически
   сохраняется в конфиг и восстанавливается при следующем запуске игры.
3. **Overlay (картинка в картинке)** — две картинки друг на друге: большая базовая снизу
   и меньшая — по центру сверху (например, рамка + иконка, аватар + бейдж и т.п.).

## Технические детали

| Параметр            | Значение                          |
|----------------------|-----------------------------------|
| Minecraft            | 1.21.4                            |
| Loader                | Fabric (`fabric-loader >= 0.19.3`) |
| Mappings              | Official Mojang Mappings          |
| Fabric API            | 0.119.4+1.21.4                    |
| Java                  | 21                                |
| Окружение             | только клиент (`environment: client`) |

---

## Установка (как игрок)

Если вы просто хотите **использовать** готовый мод:

1. Установите [Fabric Loader](https://fabricmc.net/use/) и [Fabric API](https://modrinth.com/mod/fabric-api) для 1.21.4.
2. Скачайте `.jar` из [Releases](../../releases) или со вкладки [Actions](../../actions) (артефакты сборки).
3. Положите `.jar` в папку `mods`.
4. Положите свои `.png` файлы в ресурсы мода (см. раздел ниже про размещение текстур).

---

## Как встроить в свой мод (для разработчиков)

Утилита создавалась именно как **библиотека, которую вставляют в другой проект**. Есть два пути:

### Вариант A: подключить как отдельный мод-зависимость

1. Соберите `ImageRenderUtill-1.0.0.jar` (`./gradlew build`, файл появится в `build/libs`).
2. Положите jar в `mods` вашей dev-среды (`run/mods`) или опубликуйте в свой Maven и подключите как обычную `modImplementation` зависимость.
3. В `fabric.mod.json` вашего мода добавьте зависимость:
   ```json
   "depends": {
     "imagerenderutill": "*"
   }
   ```
4. Вызывайте методы `dev.wantop.client.image.ImageManager` из своего клиентского кода.

### Вариант B: скопировать исходники прямо в свой проект (проще всего)

Так как это небольшая утилита, часто удобнее просто скопировать пакет
`dev.wantop.client.image` (и `dev.wantop.client.config`) целиком в свой мод, поменяв
`package` на свой. Тогда:

1. Скопируйте файлы:
   - `RenderedImage.java`
   - `ImageManager.java`
   - `ImageHudRenderer.java`
   - `ImageDragHandler.java`
   - `ImageUtillConfig.java`
   - `ChatScreenDragMixin.java` (и добавьте `imagerenderutill.client.mixins.json` → в ваш собственный `*.mixins.json`, секция `client`)
2. В своём `ClientModInitializer` зарегистрируйте рендерер:
   ```java
   HudRenderCallback.EVENT.register((graphics, tickCounter) -> ImageHudRenderer.render(graphics));
   ```
3. Пользуйтесь `ImageManager`, как описано ниже.

---

## Куда класть .png файлы

Картинки должны лежать как обычные ресурсы Minecraft-мода — **в `resources`**, а не в корне репозитория:

```
src/main/resources/assets/<ваш_namespace>/textures/gui/moya_kartinka.png
```

Например, если ваш modid — `mymod`, а файл называется `logo.png`, положите его так:

```
src/main/resources/assets/mymod/textures/gui/logo.png
```

и обращайтесь к нему как:

```java
ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/logo.png")
```

> В `fabric.mod.json` дополнительно ничего регистрировать не нужно — Minecraft сам подхватывает
> все текстуры из `assets/<namespace>/textures/...` при старте игры.

В этом репозитории для демонстрации уже лежат 4 тестовые картинки (просто цветные квадраты)
в `src/main/resources/assets/imagerenderutill/textures/gui/` — замените их своими и меняйте
пути в примерах ниже.

---

## Использование API (`ImageManager`)

Всё управление — через статический класс `dev.wantop.client.image.ImageManager`.
Вызывайте эти методы там, где вам удобно: в `onInitializeClient()`, по игровому событию,
по вашей команде и т.д.

### 1. Статичная картинка (позиция + размер)

```java
ImageManager.showImage(
    "mymod:logo",                                        // уникальный id
    ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/logo.png"),
    10, 10,      // x, y — позиция левого верхнего угла в пикселях экрана
    64, 64       // width, height — размер в пикселях
);

// Позже можно изменить позицию/размер/видимость:
ImageManager.setPosition("mymod:logo", 20, 20);
ImageManager.setSize("mymod:logo", 128, 128);
ImageManager.setOpacity("mymod:logo", 0.5f); // полупрозрачность
ImageManager.setVisible("mymod:logo", false); // скрыть
ImageManager.remove("mymod:logo");            // убрать совсем
```

### 2. Перетаскиваемая мышкой картинка

Двигается зажатием ЛКМ **пока открыт чат**. Позиция автоматически сохраняется
в `config/imagerenderutill.json` и восстанавливается при следующем запуске игры.

```java
ImageManager.showDraggableImage(
    "mymod:companion",
    ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/companion.png"),
    100, 100,   // позиция по умолчанию (используется только при первом запуске,
                //  если для этого id ещё нет сохранённой позиции)
    48, 48
);
```

Больше ничего делать не нужно — перетаскивание и сохранение работают "из коробки"
через встроенный миксин на экран чата.

### 3. Overlay — картинка на картинке

```java
ImageManager.showOverlayImages(
    "mymod:frame", ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/frame.png"),   // база (больше, снизу)
    "mymod:icon",  ResourceLocation.fromNamespaceAndPath("mymod", "textures/gui/icon.png"),     // оверлей (меньше, сверху, по центру)
    10, 100,      // x, y базовой картинки
    96, 96,       // ширина/высота базовой картинки
    48, 48        // ширина/высота накладываемой картинки
);
```

Внутри это просто создаёт две обычные картинки через `showImage`, но выставляет им
разные `layer` (слой отрисовки), поэтому оверлей всегда рисуется поверх базы,
даже если вы вызовете `showImage` для чего-то ещё между ними. Слоем можно управлять
вручную через `RenderedImage#setLayer(int)`, если нужна более сложная композиция.

### Полезные методы

| Метод | Что делает |
|---|---|
| `showImage(id, texture, x, y, w, h)` | Показать статичную картинку |
| `showDraggableImage(id, texture, defX, defY, w, h)` | Показать перетаскиваемую картинку с сохранением позиции |
| `showOverlayImages(...)` | Показать пару "база + оверлей" |
| `setPosition(id, x, y)` | Изменить позицию |
| `setSize(id, w, h)` | Изменить размер |
| `setOpacity(id, 0..1)` | Изменить прозрачность |
| `setVisible(id, bool)` | Показать/скрыть без удаления |
| `remove(id)` | Полностью удалить картинку |
| `get(id)` | Получить `Optional<RenderedImage>` для тонкой настройки |

---

## Сборка проекта локально

```bash
git clone https://github.com/<ваш-аккаунт>/ImageRenderUtill.git
cd ImageRenderUtill
./gradlew build
```

Готовый `.jar` появится в `build/libs/`.

Для запуска тестового клиента с модом:

```bash
./gradlew runClient
```

## Сборка через GitHub Actions

В репозитории настроен workflow `.github/workflows/build.yml`: при каждом `push` и `pull request`
проект автоматически собирается на `ubuntu-24.04` с JDK 21, а собранные `.jar` файлы
доступны на вкладке **Actions → (выбранный запуск) → Artifacts**.

---

## Структура проекта

```
src/
├── main/
│   ├── java/dev/wantop/
│   │   └── ImageRenderUtill.java          # общие константы (MOD_ID, LOGGER, id())
│   └── resources/
│       ├── fabric.mod.json
│       └── assets/imagerenderutill/textures/gui/   # сюда кладутся .png
└── client/
    ├── java/dev/wantop/client/
    │   ├── ImageRenderUtillClient.java     # entrypoint, регистрация рендера + примеры использования
    │   ├── image/
    │   │   ├── RenderedImage.java          # модель одной картинки (x, y, w, h, layer, opacity...)
    │   │   ├── ImageManager.java           # публичный API — основной класс для управления
    │   │   ├── ImageHudRenderer.java       # отрисовка картинок поверх HUD
    │   │   └── ImageDragHandler.java       # логика перетаскивания мышкой
    │   ├── config/
    │   │   └── ImageUtillConfig.java       # чтение/запись config/imagerenderutill.json
    │   └── mixin/
    │       └── ChatScreenDragMixin.java    # хук на мышь в экране чата для перетаскивания
    └── resources/
        └── imagerenderutill.client.mixins.json
```

## Лицензия

Этот проект доступен по лицензии CC0 (см. `LICENSE`) — можно свободно использовать
и встраивать в свои проекты.
