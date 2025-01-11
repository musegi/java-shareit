package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl  implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.mapToItem(itemDto);
        if (!userRepository.getAll().contains(userId)) {
            throw new NotFoundException("Не найден пользоватль с id=" + userId);
        }
        item.setOwner(userId);
        return ItemMapper.mapToItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден предмет id=" + itemId));
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Указан неверный владелец предмета");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setId(itemId);
        return ItemMapper.mapToItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        return ItemMapper.mapToItemDto(itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id=" + itemId)));
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }

    @Override
    public List<ItemDto> getByContext(String text, Long userId) {
        if (text.isBlank()) return List.of();
        List<Item> items = itemRepository.getByContext(text);
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        Collection<Item> items = itemRepository.getAll();
        return items.stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
