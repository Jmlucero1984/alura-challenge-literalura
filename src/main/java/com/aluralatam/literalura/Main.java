package com.aluralatam.literalura;

import com.aluralatam.literalura.Model.Author;
import com.aluralatam.literalura.Model.Book;
import com.aluralatam.literalura.Model.BookData;
import com.aluralatam.literalura.Model.Results;
import com.aluralatam.literalura.Repository.AuthorRepository;
import com.aluralatam.literalura.Repository.BookRepository;
import com.aluralatam.literalura.Service.APIConsumer;
import com.aluralatam.literalura.Service.DataConvert;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    Scanner sc = new Scanner(System.in);
    APIConsumer consumoAPI = new APIConsumer();
    private final String URL_BASE = "http://gutendex.com/books/?search=";
    DataConvert convierteDatos = new DataConvert();
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private List<Book> libros;
    private List<Author> autores;

    public Main(BookRepository libroRepository, AuthorRepository autorRepository) {
        this.bookRepository = libroRepository;
        this.authorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    Opcion: 
                    """;
            System.out.print(menu);
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:listarAutoresVivosEnUnDeterminadoAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
                    break;
            }
        }

    }

    private void listarLibrosPorIdioma() {
        System.out.println("Seleccione el idioma del libro, de los siguientes idiomas disponibles: ");
        List<String> languages = bookRepository.findAllLanguages();
        languages.stream()
                .forEach(System.out::println);
        System.out.println("Ingrese el idioma del libro: ");
        String idioma = sc.nextLine();

        libros = bookRepository.findByLanguagesWhereOption(idioma);

        if (!libros.isEmpty()) {
            libros.stream()
                    .sorted(Comparator.comparing(Book::getIdBook))
                    .forEach(System.out::println);
        }else{
            System.out.println("No se encontraron libros con el idioma ingresado");
        }
    }

    private void listarAutoresVivosEnUnDeterminadoAnio() {
        System.out.println("Ingrese un año para buscar autores vivos en un determinado año: ");
        Integer livingYear = sc.nextInt();
        sc.nextLine();
        autores = authorRepository.findByLivingAuthorGivenYear(livingYear);
        if (!autores.isEmpty()) {
            autores.stream()
                    .sorted(Comparator.comparing(Author::getBirthYear))
                    .forEach(System.out::println);
        }else{
            System.out.println("No se encontraron autores vivos en el año: "+livingYear);
        }

    }

    private void listarAutoresRegistrados() {
        autores = authorRepository.findByTypeEqualsAuthor();
        if (!autores.isEmpty()) {
            autores.stream()
                    .sorted(Comparator.comparing(Author::getBirthYear))
                    .forEach(System.out::println);
        }else{
            System.out.println("No hay autores registrados");
        }

    }

    private void listarLibrosRegistrados() {
        libros = bookRepository.findAll();
        if (!libros.isEmpty()) {
            libros.stream()
                    .sorted(Comparator.comparing(Book::getIdBook))
                    .forEach(System.out::println);
        }
        System.out.println("No hay libros registrados");
    }

    private void buscarLibroPorTitulo() {
        Results datosResults = getDatosLibro();
        System.out.println(datosResults);


        if (datosResults != null) {
            List<BookData> datosLibroList = datosResults.results();
            for (BookData datosLibro : datosLibroList) {
                Set<Author> autores = datosLibro.authors().stream()
                        .map(da -> {
                            System.out.println("EL NOMBRE PEDIDO ES: "+da.getName());
                            Author autor = authorRepository.findByName(da.getName());
                            System.out.println("EL AUTOR ENCONTRADO ES: "+autor);
                            if (autor == null) {
                                System.out.println("CREANDO AUTHOR");
                                autor = new Author(da.getName(),
                                        da.getBirthYear() != null ? da.getBirthYear().toString() : "N/A",
                                        da.getDeathYear() != null ? da.getDeathYear().toString() : "N/A",
                                        "author");
                                authorRepository.save(autor);
                            }

                            return autor;
                        })
                        .collect(Collectors.toSet());
/*
                List<Author> translators = datosLibro.translators().stream()
                        .map(da -> {
                            Author autor = authorRepository.findByName(da.name());
                            if (autor == null) {
                                autor = new Author(da.name(),
                                        da.birthYear() != null ? da.birthYear().toString() : "N/A",
                                        da.deathYear() != null ? da.deathYear().toString() : "N/A",
                                        "translator");
                                authorRepository.save(autor);
                            }
                            return autor;
                        })
                        .collect(Collectors.toList());
                        */


                Optional<Book> libro = Optional.ofNullable(bookRepository.findByTitle(datosLibro.title()));


                if (libro.isEmpty()) {
                    Book nuevolibro = new Book(datosLibro,autores);


                    bookRepository.save(nuevolibro);
                    System.out.println("Se guardo un libro");
                } else {
                    System.out.println("El libro " + libro.get().getTitle() + " ya existe en la base de datos.");
                }
            }
        }else{
            System.out.println("No se encontraron datos con los registros ingresados");
        }

    }

    private Results getDatosLibro() {
        System.out.println("Ingrese el titulo: ");
        String titulo = sc.nextLine();
        if(titulo.equalsIgnoreCase("")) titulo="Frankenstein";
        var json = consumoAPI.fetchData(URL_BASE+titulo.replace(" ","+"));
        System.out.println("RESULTADO:" + json);
        return convierteDatos.getData(json,Results.class);
    }
}