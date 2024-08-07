"use client";
import Image from "next/image";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import Cookie from "js-cookie";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function Home() {
  const router = useRouter();

  const [errorMessage, setErrorMessage] = useState("");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const formData = new FormData(event.currentTarget);
    const email = formData.get("email");
    const password = formData.get("password");

    const response = await fetch("http://localhost:8080/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (response.ok) {
      const data = await response.json();
      const { token } = data;
      Cookie.set("token", token, { expires: 7 });
      router.push("/admin/itens");
    } else {
      setErrorMessage("Email ou senha inválida");
      //window.alert("Login failed");
    }
  }

  return (
    <main className="flex h-screen">
      <div className="bg-white w-1/3 min-w-80 p-10 space-y-14">
        <Image src="/images/logo.jpg" width={150} height={0} alt="logo"></Image>
        <div>
          <h2 className="mb-6 font-bold text-3xl">Login</h2>

          <form onSubmit={handleSubmit}>
            <Label htmlFor="email">Email</Label>
            <Input
              placeholder="exemplo@email.com"
              name="email"
              type="email"
              className="mb-2"
            />
            <Label htmlFor="password">Senha</Label>
            <Input
              placeholder="mínimo de 8 caracteres"
              name="password"
              type="password"
            />
            {errorMessage && (
              <p className="font-semibold text-red-600 mt-3 text-center">
                {errorMessage}
              </p>
            )}
            <Button type="submit" className="w-full mt-9">
              Login
            </Button>
          </form>
          <p className="text-center mt-12">
            Não possui uma conta?{" "}
            <Link
              href={"/cadastrar"}
              className="text-primary font-semibold cursor-pointer"
            >
              Cadastre-se
            </Link>
          </p>
        </div>
      </div>

      <div className="m-auto space-y-6">
        <p className="text-centerfont font-semibold text-xl">
          <i>“O conhecimento é o caminho para a excelência.” – Aristóteles</i>
        </p>
        <h2 className="text-center text-primary font-bold text-5xl">
          Bem vindo!
        </h2>
        <Image
          src="/images/login-image.png"
          width={250}
          height={0}
          alt="imagem"
          className="m-auto"
        />
      </div>
    </main>
  );
}
