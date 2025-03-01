package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> getByContext(String text, Long userId);

    List<ItemDto> getAll(Long userId);
}