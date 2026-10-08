import { Shield } from "lucide-react";

const sections = [
  {
    title: "1. Hum Kaunsa Data Collect Karte Hain?",
    content: [
      "Account Information: Naam, mobile number, email address — jo aap registration ke waqt dete hain.",
      "Location Data: Aapki GPS location — nearby stores dhundne ke liye. Yeh sirf app use karte waqt collect hoti hai.",
      "Usage Data: App mein kya search kiya, kaunse stores dekhe, reservations — yeh data app improve karne ke liye use hota hai.",
      "Device Information: Device type, OS version, app version — technical support ke liye.",
    ],
  },
  {
    title: "2. Data Ka Use Kaise Hota Hai?",
    content: [
      "Nearby stores aur products dhundne ke liye location use hoti hai.",
      "Reservation confirmations aur updates ke liye notifications bheje jaate hain.",
      "App experience improve karne ke liye usage patterns analyze kiye jaate hain.",
      "Customer support provide karne ke liye contact information use hoti hai.",
      "Aapka data kabhi bhi third parties ko becha nahi jaata.",
    ],
  },
  {
    title: "3. Data Sharing",
    content: [
      "Store Owners: Jab aap kisi store pe reservation karte ho, toh aapka naam aur phone number us store owner ko share hota hai — pickup ke liye.",
      "Service Providers: Hum trusted third-party services (jaise cloud hosting, analytics) use karte hain jo strict confidentiality agreements ke under kaam karte hain.",
      "Legal Requirements: Agar law require kare toh data share kiya ja sakta hai.",
      "Hum aapka data kabhi bhi marketing purposes ke liye third parties ko nahi dete.",
    ],
  },
  {
    title: "4. Data Security",
    content: [
      "Saara data encrypted form mein store hota hai.",
      "OTP-based authentication se unauthorized access prevent hota hai.",
      "JWT tokens secure session management ke liye use hote hain.",
      "Regular security audits kiye jaate hain.",
      "Koi bhi data breach hone pe aapko turant notify kiya jaayega.",
    ],
  },
  {
    title: "5. Aapke Rights",
    content: [
      "Access: Aap apna data dekh sakte ho — app settings mein jaao.",
      "Correction: Galat information correct karwa sakte ho.",
      "Deletion: Account delete karne pe saara personal data remove ho jaata hai.",
      "Portability: Apna data export kar sakte ho.",
      "Opt-out: Marketing notifications se kabhi bhi opt-out kar sakte ho.",
    ],
  },
  {
    title: "6. Cookies & Tracking",
    content: [
      "Hum website pe basic analytics cookies use karte hain.",
      "App mein local storage use hota hai session maintain karne ke liye.",
      "Third-party tracking cookies use nahi kiye jaate.",
      "Browser settings se cookies disable kar sakte ho.",
    ],
  },
  {
    title: "7. Children's Privacy",
    content: [
      "AasPaas 13 saal se kam umra ke bachon ke liye nahi hai.",
      "Agar hume pata chale ki kisi minor ka data collect hua hai, toh hum use turant delete kar denge.",
    ],
  },
  {
    title: "8. Policy Changes",
    content: [
      "Is policy mein changes hone pe aapko email ya app notification se inform kiya jaayega.",
      "Major changes ke liye explicit consent liya jaayega.",
      "Last updated: January 2025",
    ],
  },
];

export default function PrivacyPolicy() {
  return (
    <div className="pt-16">
      <section className="bg-gradient-to-br from-orange-50 via-white to-amber-50 py-20">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <div className="w-14 h-14 bg-brand-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <Shield className="w-7 h-7 text-brand-500" />
          </div>
          <h1 className="text-5xl font-extrabold text-gray-900 mb-4">Privacy Policy</h1>
          <p className="text-gray-500">Last updated: January 2025</p>
          <p className="text-gray-500 mt-3 text-sm max-w-xl mx-auto">
            AasPaas Wala mein aapki privacy hamari priority hai. Yeh policy explain karti hai ki hum aapka data kaise collect, use, aur protect karte hain.
          </p>
        </div>
      </section>

      <section className="py-16 bg-white">
        <div className="max-w-3xl mx-auto px-4 sm:px-6">
          <div className="space-y-10">
            {sections.map((s) => (
              <div key={s.title} className="border-b border-gray-100 pb-10 last:border-0">
                <h2 className="text-xl font-bold text-gray-900 mb-4">{s.title}</h2>
                <ul className="space-y-2">
                  {s.content.map((c, i) => (
                    <li key={i} className="flex items-start gap-3 text-sm text-gray-600 leading-relaxed">
                      <span className="w-1.5 h-1.5 bg-brand-400 rounded-full mt-2 flex-shrink-0" />
                      {c}
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>

          <div className="mt-12 bg-brand-50 rounded-2xl p-6 border border-brand-100">
            <p className="font-semibold text-gray-900 mb-1">Koi sawaal hai privacy ke baare mein?</p>
            <p className="text-sm text-gray-500">Email karo: <a href="mailto:privacy@aaspaas.in" className="text-brand-500 hover:underline">privacy@aaspaas.in</a></p>
          </div>
        </div>
      </section>
    </div>
  );
}
