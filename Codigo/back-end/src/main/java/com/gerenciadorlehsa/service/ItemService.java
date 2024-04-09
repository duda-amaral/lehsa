package com.gerenciadorlehsa.service;

import com.gerenciadorlehsa.entity.Item;
import com.gerenciadorlehsa.entity.enums.TipoItem;
import com.gerenciadorlehsa.exceptions.lancaveis.DeletarEntidadeException;
import com.gerenciadorlehsa.exceptions.lancaveis.EntidadeNaoEncontradaException;
import com.gerenciadorlehsa.exceptions.lancaveis.TipoItemNaoEncontradoException;
import com.gerenciadorlehsa.repository.ItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.gerenciadorlehsa.util.ConstantesTopicosUtil.ITEM_SERVICE;
import static java.lang.String.format;

@Service
@Slf4j(topic = ITEM_SERVICE)
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    private final String diretorioImgs = "Codigo/back-end/src/main/java/com/gerenciadorlehsa/util/imgs";

    public byte[] encontrarImagemPorId(@NotNull UUID id) {
        log.info(">>> encontrarImagemPorId: encontrando imagem por id");
        Item itemImagem = encontrarPorId(id);
        try {
            return getImage(diretorioImgs, itemImagem.getNomeImg());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private byte[] getImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        } else {
            return null; // Handle missing images
        }
    }

    public Item encontrarPorId(@NotNull UUID id) {
        log.info(">>> encontrarPorId: encontrando item por id");
        return this.itemRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(format("item não encontrado, id: %s", id)));
    }

    public List<Item> listarTodos() {
        log.info(">>> listarTodos: listando todos itens");
        return this.itemRepository.findAll();
    }

    @Transactional
    public Item criar (@NotNull Item item, MultipartFile img) {
        log.info(">>> criar: criando item");

        try {
            String nomeImagem = saveImageToStorage(diretorioImgs, img);
            item.setNomeImg(nomeImagem);
            item.setId(null);
            item = this.itemRepository.save(item);
            log.info(format(">>> criar: item criado, id: %s", item.getId()));
            return item;
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public String saveImageToStorage(String uploadDirectory, MultipartFile imageFile) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    @Transactional
    public Item atualizar (Item item, MultipartFile img) {
        log.info(">>> atualizar: atualizando item");
        Item itemExistente = encontrarPorId(item.getId());
        List<String> propriedadesNulas = new ArrayList<>();
        log.info("img é null?" + (img.getContentType()));
        if (img.getContentType() == null) {
            propriedadesNulas.add("nomeImagem");
        } else {
            try {
                deleteImage(diretorioImgs, itemExistente.getNomeImg());
                String nomeImagem = saveImageToStorage(diretorioImgs, img);
                item.setNomeImg(nomeImagem);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        propriedadesNulas.addAll(getNullPropertyNames(item));
        String[] arrayS = new String[propriedadesNulas.size()];

        BeanUtils.copyProperties(item, itemExistente, propriedadesNulas.toArray(arrayS));
        itemExistente = this.itemRepository.save(itemExistente);
        log.info(format(">>> atualizar: item atualizado, id: %s", item.getId()));
        return itemExistente;
    }

    private List<String> getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());

        }

        return emptyNames.stream().toList();
    }

    public void deletar (@NotNull UUID id) {
        log.info(">>> deletar: deletando item");
        Item item = encontrarPorId(id);
        try {
            deleteImage("Codigo/back-end/src/main/java/com/gerenciadorlehsa/util/imgs",
                    item.getNomeImg());
            this.itemRepository.deleteById(id);
            log.info(format(">>> deletar: item deletado, id: %s", id));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new DeletarEntidadeException(format("existem entidades relacionadas: %s", e));
        }
    }

    public String deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return "Success";
        } else {
            return "Failed"; // Handle missing images
        }
    }

    public List<Item> encontrarPorTipo (@NotNull String tipo) {
        log.info(">>> encontrarPorTipo: encontrando itens com o tipo especificado");
        tipo = tipo.toUpperCase();
        try {
            TipoItem enumTipo = Enum.valueOf(TipoItem.class, tipo);
            return this.itemRepository.findByTipoItem(enumTipo);
        } catch (IllegalArgumentException e) {
            throw  new TipoItemNaoEncontradoException(format("não existe o tipo passado: " + tipo));
        }
    }

    public List<Item> encontrarPorNome (@NotNull String nome) {
        log.info(">>> encontrarPorNome: encontrando itens com o nome especificado");
        return this.itemRepository.findByNome(nome);
    }
}