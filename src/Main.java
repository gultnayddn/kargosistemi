import java.util.*;

// Müşteri sınıfı
class Musteri {
    int musteriID;
    String isim;
    String soyisim;
    LinkedList<Gonderi> gonderiGecmisi;

    public Musteri(int musteriID, String isim, String soyisim) {
        this.musteriID = musteriID;
        this.isim = isim;
        this.soyisim = soyisim;
        this.gonderiGecmisi = new LinkedList<>();
    }

    // Gönderi ekleme (tarih sırasına göre ekler)
    public void gonderiEkle(Gonderi yeniGonderi) {
        int i = 0;
        while (i < gonderiGecmisi.size() && gonderiGecmisi.get(i).tarih.before(yeniGonderi.tarih)) {
            i++;
        }
        gonderiGecmisi.add(i, yeniGonderi);
    }

    // Gönderim geçmişini listeleme
    public void gonderiGecmisiniListele() {
        if (gonderiGecmisi.isEmpty()) {
            System.out.println("Gonderi gecmisi bos.");
        } else {
            for (Gonderi gonderi : gonderiGecmisi) {
                System.out.println("Gonderi ID: " + gonderi.gonderiID + ", Tarih: " + gonderi.tarih + ", Durum: " + gonderi.teslimDurumu + ", Sure: " + gonderi.teslimSuresi + " gün");
            }
        }
    }
}

// Gönderi sınıfı
class Gonderi {
    int gonderiID;
    Date tarih;
    String teslimDurumu;
    int teslimSuresi;

    public Gonderi(int gonderiID, Date tarih, String teslimDurumu, int teslimSuresi) {
        this.gonderiID = gonderiID;
        this.tarih = tarih;
        this.teslimDurumu = teslimDurumu;
        this.teslimSuresi = teslimSuresi;
    }
}

// Öncelikli Kargo sınıfı (Priority Queue kullanımı için)
class OncelikliKargo implements Comparable<OncelikliKargo> {
    int gonderiID;
    int teslimSuresi;
    String kargoDurumu;

    public OncelikliKargo(int gonderiID, int teslimSuresi, String kargoDurumu) {
        this.gonderiID = gonderiID;
        this.teslimSuresi = teslimSuresi;
        this.kargoDurumu = kargoDurumu;
    }

    @Override
    public int compareTo(OncelikliKargo o) {
        return Integer.compare(this.teslimSuresi, o.teslimSuresi);
    }
}

// Teslimat rotaları için düğüm sınıfı (Tree yapısı için)
class Sehir {
    String sehirAdi;
    int sehirID;
    List<Sehir> altSehirler;
    int teslimSuresi;

    public Sehir(String sehirAdi, int sehirID, int teslimSuresi) {
        this.sehirAdi = sehirAdi;
        this.sehirID = sehirID;
        this.altSehirler = new ArrayList<>();
        this.teslimSuresi = teslimSuresi;
    }

    public void altSehirEkle(Sehir altSehir) {
        altSehirler.add(altSehir);
    }

    public void agaciGoruntule(String prefix) {
        System.out.println(prefix + "- " + sehirAdi + " (Teslim Sure: " + teslimSuresi + " gun)");
        for (Sehir altSehir : altSehirler) {
            altSehir.agaciGoruntule(prefix + "    ");
        }
    }

    public int enKisaTeslimatSuresi() {
        int minSure = this.teslimSuresi;
        for (Sehir altSehir : altSehirler) {
            minSure = Math.min(minSure, altSehir.enKisaTeslimatSuresi());
        }
        return minSure;
    }
}

// Ana sınıf
class KargoTakipSistemi {
    List<Musteri> musteriler;
    PriorityQueue<OncelikliKargo> oncelikliKargolar;
    Stack<Gonderi> gonderimGecmisi;
    Sehir merkez;
    Scanner scanner;

    public KargoTakipSistemi() {
        this.musteriler = new ArrayList<>();
        this.oncelikliKargolar = new PriorityQueue<>();
        this.gonderimGecmisi = new Stack<>();
        this.merkez = new Sehir("Merkez", 0, 5);
        this.scanner = new Scanner(System.in);
    }

    public void musteriEkle() {
        System.out.print("Musteri ID: ");
        int musteriID = scanner.nextInt();
        scanner.nextLine();
        System.out.print("İsim: ");
        String isim = scanner.nextLine();
        System.out.print("Soyisim: ");
        String soyisim = scanner.nextLine();
        musteriler.add(new Musteri(musteriID, isim, soyisim));
        System.out.println("Yeni musteri eklendi.");
    }

    public void gonderiEkle() {
        System.out.print("Musteri ID: ");
        int musteriID = scanner.nextInt();
        Musteri musteri = musteriler.stream().filter(m -> m.musteriID == musteriID).findFirst().orElse(null);
        if (musteri == null) {
            System.out.println("Musteri bulunamadi.");
            return;
        }
        System.out.print("Gonderi ID: ");
        int gonderiID = scanner.nextInt();
        System.out.print("Teslim Sure (gun): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Teslim Durumu (Teslim Edildi/Teslim Edilmedi): ");
        String teslimDurumu = scanner.nextLine();
        Gonderi yeniGonderi = new Gonderi(gonderiID, new Date(), teslimDurumu, teslimSuresi);
        musteri.gonderiEkle(yeniGonderi);
        gonderimGecmisi.push(yeniGonderi);
        System.out.println("Gonderi basariyla eklendi.");
    }

    public void oncelikliKargoEkle() {
        System.out.print("Gonderi ID: ");
        int gonderiID = scanner.nextInt();
        System.out.print("Teslim Sure (gun): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Kargo Durumu (Teslimatta/Teslim Edildi): ");
        String kargoDurumu = scanner.nextLine();
        oncelikliKargolar.add(new OncelikliKargo(gonderiID, teslimSuresi, kargoDurumu));
        System.out.println("Kargo oncelikli siraya eklendi.");
    }

    public void teslimatRotalariniGoruntule() {
        System.out.println("Teslimat Rotalari:");
        merkez.agaciGoruntule("");
    }

    public void sehirEkle() {
        System.out.print("Sehir Adı: ");
        String sehirAdi = scanner.nextLine();
        System.out.print("Sehir ID: ");
        int sehirID = scanner.nextInt();
        System.out.print("Teslim Sure (gun): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine();
        Sehir yeniSehir = new Sehir(sehirAdi, sehirID, teslimSuresi);
        merkez.altSehirEkle(yeniSehir);
        System.out.println("Sehir basariyla eklendi.");
    }

    public void sonBesGonderiyiGoruntule() {
        if (gonderimGecmisi.isEmpty()) {
            System.out.println("Gonderim gecmisi bos.");
            return;
        }
        System.out.println("Son 5 Gonderi:");
        List<Gonderi> gecmisKopya = new ArrayList<>(gonderimGecmisi);
        for (int i = gecmisKopya.size() - 1; i >= Math.max(0, gecmisKopya.size() - 5); i--) {
            Gonderi gonderi = gecmisKopya.get(i);
            System.out.println("Gonderi ID: " + gonderi.gonderiID + ", Tarih: " + gonderi.tarih + ", Durum: " + gonderi.teslimDurumu);
        }
    }

    public void kargoDurumuSorgula() {
        System.out.print("Teslim Edildi mi? (Evet/Hayır): ");
        String durum = scanner.nextLine();
        boolean teslimEdildi = durum.equalsIgnoreCase("Evet");

        List<Gonderi> tumGonderiler = new ArrayList<>();
        for (Musteri musteri : musteriler) {
            tumGonderiler.addAll(musteri.gonderiGecmisi);
        }

        if (teslimEdildi) {
            tumGonderiler.sort(Comparator.comparingInt(g -> g.gonderiID));
            System.out.println("Teslim Edilmis Kargolar (ID sirasina gore):");
            for (Gonderi g : tumGonderiler) {
                if (g.teslimDurumu.equalsIgnoreCase("Teslim Edildi")) {
                    System.out.println("Gonderi ID: " + g.gonderiID);
                }
            }
        } else {
            tumGonderiler.sort(Comparator.comparingInt(g -> g.teslimSuresi));
            System.out.println("Teslim Edilmemis Kargolar (Teslim Suresi sirasina gore):");
            for (Gonderi g : tumGonderiler) {
                if (g.teslimDurumu.equalsIgnoreCase("Teslim Edilmedi")) {
                    System.out.println("Gonderi ID: " + g.gonderiID + ", Teslim Suresi: " + g.teslimSuresi + " gun");
                }
            }
        }
    }
    // Teslimat rotalarını kullanıcıdan almak ve ağaç yapısında görselleştirmek için güncellenmiş sınıf
    public void sehirRotalariniOlustur() {
        System.out.print("Kac adet sehir eklemek istiyorsunuz? ");
        int sehirSayisi = scanner.nextInt();
        scanner.nextLine();

        Map<Integer, Sehir> sehirHaritasi = new HashMap<>();
        sehirHaritasi.put(merkez.sehirID, merkez);

        for (int i = 0; i < sehirSayisi; i++) {
            System.out.print("Sehir Adi: ");
            String sehirAdi = scanner.nextLine();
            System.out.print("Sehir ID: ");
            int sehirID = scanner.nextInt();
            System.out.print("Teslim Suresi (gun): ");
            int teslimSuresi = scanner.nextInt();
            scanner.nextLine();

            Sehir yeniSehir = new Sehir(sehirAdi, sehirID, teslimSuresi);
            sehirHaritasi.put(sehirID, yeniSehir);

            System.out.print(sehirAdi + " hangi sehirle baglantili (Sehir ID giriniz): ");
            int ustSehirID = scanner.nextInt();
            scanner.nextLine();

            Sehir ustSehir = sehirHaritasi.getOrDefault(ustSehirID, null);
            if (ustSehir != null) {
                ustSehir.altSehirEkle(yeniSehir);
            } else {
                System.out.println("Gecersiz ust sehir ID! Sehir merkezine baglaniyor.");
                merkez.altSehirEkle(yeniSehir);
            }
        }

        System.out.println("Tüm sehirler ve rotalar basariyla eklendi!");
    }

    public void menu() {
        while (true) {
            System.out.println("\nKargo Takip Sistemi Menu:");
            System.out.println("1. Yeni musteri ekle.");
            System.out.println("2. Kargo gonderimi ekle.");
            System.out.println("3. Kargo durumu sorgula.");
            System.out.println("4. Gönderim gecmisini goruntule.");
            System.out.println("5. Son 5 gonderiyi goruntule."); // Yeni Menü Seçeneği
            System.out.println("6. Teslimat rotalarini goster.");
            System.out.println("7. Teslimat rotalarini olustur.");
            System.out.println("8. Cikis.");
            System.out.print("Seciminizi yapiniz: ");

            int secim = scanner.nextInt();
            scanner.nextLine(); // Giriş sonrası tamponu temizle.

            switch (secim) {
                case 1 -> musteriEkle();
                case 2 -> gonderiEkle();
                case 3 -> kargoDurumuSorgula();
                case 4 -> {
                    System.out.print("Musteri ID: ");
                    int musteriID = scanner.nextInt();
                    Musteri musteri = musteriler.stream()
                            .filter(m -> m.musteriID == musteriID)
                            .findFirst()
                            .orElse(null);
                    if (musteri == null) {
                        System.out.println("Musteri bulunamadi.");
                    } else {
                        musteri.gonderiGecmisiniListele();
                    }
                }
                case 5 -> sonBesGonderiyiGoruntule(); // Yeni Menü İşlevi Bağlantısı
                case 6 -> teslimatRotalariniGoruntule();
                case 7 -> sehirRotalariniOlustur();
                case 8 -> {
                    System.out.println("Sistemden cikiliyor...");
                    return;
                }
                default -> System.out.println("Gecersiz secim. Lutfen tekrar deneyin.");
            }
        }
    }

    public static void main(String[] args) {
        KargoTakipSistemi sistem = new KargoTakipSistemi();
        sistem.menu();
    }
}
