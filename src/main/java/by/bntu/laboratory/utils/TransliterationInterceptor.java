package by.bntu.laboratory.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.util.regex.Pattern;

public class TransliterationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Преобразование URL-адреса перед выполнением обработки запроса

        String uri = request.getRequestURI();
        String[] uriParts = uri.split("/");
        for (int i = 0; i < uriParts.length; i++) {
            // Преобразование русских символов в латиницу и замена пробелов на дефисы
            String latinizedName = transliterateAndReplace(uriParts[i]);
            uriParts[i] = latinizedName;
        }

        // Обновление URL-адреса после преобразования
        String updatedUri = String.join("/", uriParts);
        request.getRequestDispatcher(updatedUri).forward(request, response);

        return true;
    }

    private String transliterateAndReplace(String text) {
        // Простейший метод транслитерации русских символов в латиницу
        String result = text.replaceAll(" ", "-")
                .replaceAll("а", "a")
                .replaceAll("б", "b")
                .replaceAll("в", "v")
                .replaceAll("г", "g")
                .replaceAll("д", "d")
                .replaceAll("е", "e")
                .replaceAll("ё", "yo")
                .replaceAll("ж", "zh")
                .replaceAll("з", "z")
                .replaceAll("и", "i")
                .replaceAll("й", "y")
                .replaceAll("к", "k")
                .replaceAll("л", "l")
                .replaceAll("м", "m")
                .replaceAll("н", "n")
                .replaceAll("о", "o")
                .replaceAll("п", "p")
                .replaceAll("р", "r")
                .replaceAll("с", "s")
                .replaceAll("т", "t")
                .replaceAll("у", "u")
                .replaceAll("ф", "f")
                .replaceAll("х", "h")
                .replaceAll("ц", "ts")
                .replaceAll("ч", "ch")
                .replaceAll("ш", "sh")
                .replaceAll("щ", "sch")
                .replaceAll("ъ", "")
                .replaceAll("ы", "y")
                .replaceAll("ь", "")
                .replaceAll("э", "e")
                .replaceAll("ю", "yu")
                .replaceAll("я", "ya")
                .replaceAll("А", "A")
                .replaceAll("Б", "B")
                .replaceAll("В", "V")
                .replaceAll("Г", "G")
                .replaceAll("Д", "D")
                .replaceAll("Е", "E")
                .replaceAll("Ё", "YO")
                .replaceAll("Ж", "ZH")
                .replaceAll("З", "Z")
                .replaceAll("И", "I")
                .replaceAll("Й", "Y")
                .replaceAll("К", "K")
                .replaceAll("Л", "L")
                .replaceAll("М", "M")
                .replaceAll("Н", "N")
                .replaceAll("О", "O")
                .replaceAll("П", "P")
                .replaceAll("Р", "R")
                .replaceAll("С", "S")
                .replaceAll("Т", "T")
                .replaceAll("У", "U")
                .replaceAll("Ф", "F")
                .replaceAll("Х", "H")
                .replaceAll("Ц", "TS")
                .replaceAll("Ч", "CH")
                .replaceAll("Ш", "SH")
                .replaceAll("Щ", "SCH")
                .replaceAll("Ъ", "")
                .replaceAll("Ы", "Y")
                .replaceAll("Ь", "")
                .replaceAll("Э", "E")
                .replaceAll("Ю", "YU")
                .replaceAll("Я", "YA");

        return result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Необходимые действия после обработки запроса
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Действия после завершения обработки запроса
    }
}
