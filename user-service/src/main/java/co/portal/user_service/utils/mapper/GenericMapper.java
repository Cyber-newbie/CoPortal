package co.portal.user_service.utils.mapper;

public interface GenericMapper<E, D, R> {

    E toEntity(D d);

    R toDto(E e, String status, String message, String token);

    // Overloaded version without token
    default R toDto(E e, String status, String message) {
        return toDto(e, status, message, null);
    }

}
