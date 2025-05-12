package AndrewWebServices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class AndrewWebServicesTest {
    private InMemoryDatabase fakeDatabase;
    private RecSys stubRecommender;
    private PromoService mockPromoService;
    private AndrewWebServices andrewWebService;

    @Before
    public void setUp() {
        // Хуурамч мэдээллийн сан үүсгэх
        fakeDatabase = new InMemoryDatabase();
        fakeDatabase.addUser("Scotty", 17214);

        // Загвар санал болгогч үүсгэх
        stubRecommender = mock(RecSys.class);
        when(stubRecommender.getRecommendation("Scotty")).thenReturn("Animal House");

        // Хуурмаг промо үйлчилгээ үүсгэх
        mockPromoService = mock(PromoService.class);

        // AndrewWebServices-ийн инстанц үүсгэхдээ хуулбар обьектуудыг ашиглах
        andrewWebService = new AndrewWebServices(fakeDatabase, stubRecommender, mockPromoService);
    }

    @Test
    public void testLogIn() {
        // Жинхэнэ мэдээллийн сангийн сааталгүйгээр логин хийхийг шалгахын тулд хуурамч мэдээллийн сан ашиглах
        assertTrue(andrewWebService.logIn("Scotty", 17214));
        assertTrue(!andrewWebService.logIn("InvalidUser", 12345));
    }

    @Test
    public void testGetRecommendation() {
        // Санал болгогчийн удаан ажиллах үйлдлийг дуурайлган, урьдчилан тодорхойлсон утга буцаах загвар ашиглах
        assertEquals("Animal House", andrewWebService.getRecommendation("Scotty"));
        // Загварын getRecommendation метод нэг удаа дуудагдсан эсэхийг шалгах
        verify(stubRecommender, times(1)).getRecommendation("Scotty");
    }

    @Test
    public void testSendEmail() {
        // Имэйл илгээх бодит үйлдлийг хийхгүйгээр promoService-ийн mailTo метод дуудагдсан эсэхийг шалгах хуурмаг ашиглах
        String testEmail = "test@example.com";
        andrewWebService.sendPromoEmail(testEmail);
        // mailTo метод testEmail аргументтайгаар нэг удаа дуудагдсан эсэхийг шалгах
        verify(mockPromoService, times(1)).mailTo(testEmail);
    }

    @Test
    public void testNoSendEmail() {
        // Ямар нэгэн нөхцөлд имэйл илгээгдэхгүй байх ёстойг шалгах (жишээ нь, амжилтгүй логины дараа)
        andrewWebService.logIn("InvalidUser", 123);
        // mailTo метод огт дуудагдаагүй эсэхийг шалгах
        verify(mockPromoService, never()).mailTo(anyString());
    }
}