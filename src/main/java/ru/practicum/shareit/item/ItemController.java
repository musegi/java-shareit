package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /items");
        List<ItemDto> response = itemService.getAll(userId);
        log.info("Получен ответ GET с телом ответа {}", response);
        return response;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST с телом запроса {}", itemDto);
        ItemDto response = itemService.createItem(itemDto, userId);
        log.info("Получен ответ POST с телом ответа {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Получен запрос PATCH с телом запроса {}", itemDto);
        ItemDto response = itemService.updateItem(itemDto, itemId, userId);
        log.info("Получен ответ PATCH с телом ответа {}", response);
        return response;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Получен запрос DELETE /items/{}", itemId);
        itemService.deleteItem(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос GET /items/{}", itemId);
        ItemDto response = itemService.getItemById(itemId, userId);
        log.info("Получен ответ GET с телом ответа {}", response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> getByContext(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(name = "text") String text) {
        log.info("Получен запрос GET /items/search с контекстом = {}", text);
        List<ItemDto> response = itemService.getByContext(text, userId);
        log.info("Получен ответ GET с телом ответа {}", response);
        return response;
    }
}