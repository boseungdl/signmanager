package com.example.signmanager.common.response;

// 다른 파일(클래스)들을 불러와서 사용합니다.
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * [공통 API 응답 포맷 클래스]
 * - 이 클래스는 서버가 클라이언트에게 보내는 모든 응답을 통일된 형식으로 만들기 위해 사용됩니다.
 * - 마치 모든 택배를 같은 종류의 상자에 담아 보내는 것과 같습니다.
 *
 * - 응답에는 다음과 같은 정보가 포함됩니다:
 * - status: HTTP 상태 코드 (예: 200, 400)
 * - success: 요청이 성공했는지(true) 실패했는지(false)
 * - message: 사용자에게 보여줄 메시지 (예: "로그인 성공", "비밀번호가 틀렸습니다")
 * - data: 실제 요청 결과 데이터 (예: 사용자 정보, 게시글 목록)
 * - errorCode: 에러 발생 시 개발자가 식별할 수 있는 고유 코드 (예: "INVALID_PASSWORD")
 *
 * - data 필드가 비어있을(null) 경우, JSON 응답에서는 해당 필드를 아예 보여주지 않습니다.
 * (이는 @JsonInclude(JsonInclude.Include.NON_NULL) 덕분입니다.)
 */
@Getter // 이 클래스의 모든 private 변수에 대해 '값을 가져오는' (getter) 메서드를 자동으로 만들어 줍니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 매개변수 없는 생성자를 만들어주되, 이 클래스 내부나 상속받은 클래스에서만 쓸 수 있게 합니다.
// (이것은 JSON 변환 시 프레임워크가 객체를 만들기 위해 필요할 수 있습니다.)
public class ApiResponse<T> {
    // 이 클래스는 <T>라는 '만능 타입'을 가집니다.
    // 이는 'data' 필드에 어떤 종류의 데이터(UserDto, String 등)든 담을 수 있다는 의미입니다.

    private int status; // HTTP 상태 코드 (예: 200, 400). 숫자로 표시됩니다.
    private boolean success; // 요청이 성공(true)했는지, 실패(false)했는지 나타냅니다.
    private String message; // 사용자에게 보여줄 메시지입니다. (예: "작업이 성공적으로 완료되었습니다.")

    @JsonInclude(JsonInclude.Include.NON_NULL) // 이 어노테이션 덕분에 'data' 필드에 값이 없으면(null) JSON 응답에서 이 필드를 아예 제거합니다.
    private T data; // 실제 응답 데이터입니다. <T> 타입이므로 어떤 데이터든 담을 수 있습니다.

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode; // 에러 상황을 개발자가 식별할 수 있는 코드입니다. (예: "USER_NOT_FOUND")

    // private 생성자:
    // 이 생성자는 오직 이 클래스 내부에서만 호출할 수 있습니다.
    // 외부에서는 이 생성자를 직접 사용해서 ApiResponse 객체를 만들 수 없습니다.
    // (이렇게 하는 이유는, 아래의 'success'나 'error' 메서드를 통해서만 객체를 만들도록 강제하여(팩토리 메서드 패턴),
    //  항상 규칙에 맞는 올바른 ApiResponse 객체가 생성되도록 하기 위함입니다.)
    private ApiResponse(HttpStatus status, boolean success, String message, T data, String errorCode) {
        this.status = status.value(); // HttpStatus(예: HttpStatus.OK)에서 숫자 값(예: 200)만 가져와 저장합니다.
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    // --- 성공 응답 생성 메서드 ---
    // 이들은 '성공 응답' 상자를 만들어주는 전문 공장 같은 메서드입니다.
    // 'static'이 붙어 있어서, ApiResponse 객체를 따로 만들 필요 없이 'ApiResponse.success(...)'처럼 바로 호출할 수 있습니다.

    // 1. 데이터가 있는 성공 응답을 만들 때 사용합니다.
    // <T>는 이 메서드가 'T'라는 어떤 타입의 데이터를 처리할 수 있는 제네릭 메서드임을 선언합니다.
    // ApiResponse<T>는 이 메서드가 'T' 타입의 데이터를 담은 ApiResponse 객체를 반환한다는 의미입니다.
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        // 내부적으로 위에서 정의한 private 생성자를 호출하여 객체를 만듭니다.
        // success는 true로 고정하고, errorCode는 성공이므로 null을 넘겨줍니다.
        return new ApiResponse<>(status, true, message, data, null);
    }

    // 2. 데이터 없이 메시지만 있는 성공 응답을 만들 때 사용합니다. (예: 삭제 성공, 204 No Content)
    // data 필드에 null을 넘겨주어 JSON 응답에서 data 필드가 제외되도록 합니다.
    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return new ApiResponse<>(status, true, message, null, null);
    }

    // --- 실패 응답 생성 메서드 ---
    // 이들은 '실패 응답' 상자를 만들어주는 전문 공장 같은 메서드입니다.

    // 1. 에러 코드와 데이터까지 있는 실패 응답을 만들 때 사용합니다.
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorCode, T data) {
        // success는 false로 고정합니다.
        return new ApiResponse<>(status, false, message, data, errorCode);
    }

    // 2. 에러 코드만 있고 데이터는 없는, 가장 일반적인 실패 응답을 만들 때 사용합니다.
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorCode) {
        return new ApiResponse<>(status, false, message, null, errorCode);
    }

    // 3. 에러 코드도 없이 메시지만 있는 간결한 실패 응답을 만들 때 사용합니다.
    // errorCode 필드에 null을 넘겨주어 JSON 응답에서 errorCode 필드가 제외되도록 합니다.
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, false, message, null, null);
    }

    // TODO: 에러 코드를 미리 정의된 목록(enum)으로 관리할 때 사용될 예시입니다. (지금은 사용하지 않음)
    // 예를 들어 ErrorCode.USER_NOT_FOUND 같은 Enum을 만들면 더 깔끔하게 에러를 처리할 수 있습니다.
    // public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
    //     return new ApiResponse<>(errorCode.getHttpStatus(), false, errorCode.getMessage(), data,
    // errorCode.getCode());
    // }
    // public static <T> ApiResponse<T> error(ErrorCode errorCode) {
    //     return new ApiResponse<>(errorCode.getHttpStatus(), false, errorCode.getMessage(), null,
    // errorCode.getCode());
    // }
}
