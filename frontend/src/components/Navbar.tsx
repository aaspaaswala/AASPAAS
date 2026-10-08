import { useState, useEffect } from "react";
import { NavLink, Link, useLocation } from "react-router-dom";
import { Menu, X, MapPin } from "lucide-react";

const links = [
  { label: "Home", href: "/" },
  { label: "About", href: "/about" },
  { label: "For Customers", href: "/for-customers" },
  { label: "For Business", href: "/for-business" },
  { label: "FAQ", href: "/faq" },
  { label: "Contact", href: "/contact" },
];

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const location = useLocation();
  const isHome = location.pathname === "/";

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    setOpen(false);
  }, [location]);

  const navBg = isHome
    ? scrolled ? "bg-white/95 backdrop-blur-sm shadow-sm" : "bg-transparent"
    : "bg-white shadow-sm";

  return (
    <nav className={`fixed top-0 w-full z-50 transition-all duration-300 ${navBg}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-brand-500 rounded-lg flex items-center justify-center">
              <MapPin className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-bold text-gray-900">AasPaas</span>
          </Link>

          <div className="hidden md:flex items-center gap-6">
            {links.map((l) => (
              <NavLink
                key={l.label}
                to={l.href}
                className={({ isActive }) =>
                  `text-sm font-medium transition-colors ${isActive ? "text-brand-500" : "text-gray-600 hover:text-brand-500"}`
                }
              >
                {l.label}
              </NavLink>
            ))}
            <Link to="/inquiry" className="bg-brand-500 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-brand-600 transition-colors">
              Inquiry
            </Link>
          </div>

          <button className="md:hidden" onClick={() => setOpen(!open)}>
            {open ? <X className="w-6 h-6 text-gray-700" /> : <Menu className="w-6 h-6 text-gray-700" />}
          </button>
        </div>
      </div>

      {open && (
        <div className="md:hidden bg-white border-t px-4 py-4 flex flex-col gap-3">
          {links.map((l) => (
            <NavLink
              key={l.label}
              to={l.href}
              className={({ isActive }) =>
                `text-sm font-medium py-1 ${isActive ? "text-brand-500" : "text-gray-700"}`
              }
            >
              {l.label}
            </NavLink>
          ))}
          <Link to="/inquiry" className="bg-brand-500 text-white px-4 py-2 rounded-lg text-sm font-semibold text-center mt-2">
            Inquiry
          </Link>
        </div>
      )}
    </nav>
  );
}
