"use client";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import Cookie from "js-cookie";
import Link from "next/link";
import { translateStatus } from "@/utils/translateStatus";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { showSuccessMessage } from "@/utils/toast";

type Solicitante = {
  id: string;
  nome: string;
  telefone: string;
  email: string;
  curso: string | null;
};

type Tecnico = {
  email: string;
};

type Item = {
  id: string;
  nome: string;
  tipo_item: string;
  quantidade: number;
};

type AgendamentoProps = {
  id: string;
  statusTransacaoItem: string;
  dataHoraInicio: string;
  dataHoraFim: string;
  tecnico: Tecnico | null;
  solicitantes: Solicitante[];
  itens: Item[];
  observacaoSolicitacao: string;
};

const fetchItem = async (id: string | string[]) => {
  try {
    const token = Cookie.get("token");
    const response = await fetch(`http://localhost:8080/agendamento/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Failed to fetch item");
    }

    return await response.json();
  } catch (error) {
    console.error("Failed to fetch item:", error);
    return null;
  }
};

const ValidarAgendamento = () => {
  const [agendamento, setAgendamento] = useState<AgendamentoProps | null>(null);
  const [tecnicoEmail, setTecnicoEmail] = useState<string>("");

  const handleAcao = async (status: string) => {
    try {
      const token = Cookie.get("token");
      const response = await fetch(
        `http://localhost:8080/agendamento/${agendamento?.id}/${status}`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (!response.ok) {
        throw new Error("Failed to update status");
      }

      setAgendamento({
        ...agendamento!,
        statusTransacaoItem: status,
      });
    } catch (error) {
      console.error("Error updating status:", error);
    }
  };

  const handleAddTecnico = async () => {
    try {
      const token = Cookie.get("token");
      const response = await fetch(
        `http://localhost:8080/agendamento/${agendamento?.id}/tecnico/${tecnicoEmail}`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (!response.ok) {
        throw new Error("Failed to update tecnico");
      }

      showSuccessMessage("Técnico adicionado!");
      setTimeout(() => {
        window.location.reload();
      }, 1000);

      const data = await response.json();

      setAgendamento({
        ...agendamento!,
        tecnico: data.tecnico,
      });
    } catch (error) {
      console.error("Error updating tecnico:", error);
    }
  };

  useEffect(() => {
    const fetchItemData = async () => {
      const router = window.location.pathname;
      const id = router.split("/")[3];
      const status = router.split("/")[4];

      if (!id) return;

      const data = await fetchItem(id);
      setAgendamento(data);

      if (status) {
        handleAcao(status);
      }

      console.log(data);
    };

    fetchItemData();
  }, []);

  return (
    <>
      <main>
        <div className="container mx-auto px-4 py-8">
          <div className="bg-white dark:bg-gray-950 rounded-lg shadow overflow-hidden">
            <div className="px-6 py-8">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h2 className="text-xl font-bold mb-2">
                    Detalhes do agendamento
                  </h2>
                  <ul className="text-gray-500 dark:text-gray-400">
                    <li className="flex space-x-2">
                      <h2 className="font-bold">Data de início:</h2>
                      <p>{agendamento?.dataHoraInicio}</p>
                    </li>
                    <li className="flex space-x-2">
                      <h2 className="font-bold">Data final:</h2>
                      <p>{agendamento?.dataHoraFim}</p>
                    </li>
                    <li className="flex space-x-2">
                      <h2 className="font-bold">Status:</h2>
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          agendamento?.statusTransacaoItem === "APROVADO"
                            ? "bg-green-100 text-green-800"
                            : agendamento?.statusTransacaoItem === "EM_ANALISE"
                            ? "bg-yellow-100 text-yellow-800"
                            : agendamento?.statusTransacaoItem === "RECUSADO"
                            ? "bg-red-100 text-red-800"
                            : "bg-gray-500 text-white"
                        }`}
                      >
                        {translateStatus(
                          agendamento?.statusTransacaoItem || "",
                        )}
                      </span>
                    </li>
                    <li className="flex space-x-2">
                      <h2 className="font-bold">Técnico:</h2>
                      <p>
                        {agendamento?.tecnico
                          ? `${agendamento.tecnico.email}`
                          : "Nenhum técnico designado"}
                      </p>
                    </li>
                    <li className="flex space-x-2">
                      <h2 className="font-bold">Observação:</h2>
                      <p>{agendamento?.observacaoSolicitacao}</p>
                    </li>
                  </ul>
                </div>
                <div className="flex items-center space-x-4 mb-14">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button variant="outline">Adicionar técnico</Button>
                    </DialogTrigger>
                    <DialogContent className="sm:max-w-[425px]">
                      <DialogHeader>
                        <DialogTitle>Adicionar técnico</DialogTitle>
                        <DialogDescription>
                          Insira o e-mail do técnico abaixo e clique em Salvar
                          para adicionar.
                        </DialogDescription>
                      </DialogHeader>
                      <div className="grid gap-4 py-4">
                        <div className="grid grid-cols-4 items-center gap-4">
                          <Label htmlFor="name" className="text-right">
                            E-mail
                          </Label>
                          <Input
                            id="tecnico-email"
                            placeholder="ex@email.com"
                            className="col-span-3"
                            value={tecnicoEmail}
                            onChange={(e) => setTecnicoEmail(e.target.value)}
                          />
                        </div>
                      </div>
                      <DialogFooter>
                        <DialogClose>
                          <Button type="submit" onClick={handleAddTecnico}>
                            Salvar
                          </Button>
                        </DialogClose>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                  <Button
                    type="submit"
                    className="w-full sm:w-auto bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline"
                    onClick={() => handleAcao("APROVADO")}
                  >
                    Aprovar
                  </Button>
                  <Button
                    type="submit"
                    className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline"
                    onClick={() => handleAcao("RECUSADO")}
                  >
                    Recusar
                  </Button>
                </div>
              </div>
              <Separator />
              <div className="grid grid-cols-2 gap-4 mt-4">
                {agendamento?.itens.map((item, id) => (
                  <div key={id}>
                    <h3 className="text-lg font-bold mb-2">Item</h3>
                    <ul className="text-gray-500 dark:text-gray-400">
                      <li className="flex space-x-2">
                        <h2 className="font-bold">Nome do item:</h2>
                        <p>{item.nome}</p>
                      </li>

                      <li className="flex space-x-2">
                        <h2 className="font-bold">Categoria:</h2>
                        <p>{item.tipo_item}</p>
                      </li>

                      <li className="flex space-x-2">
                        <h2 className="font-bold">
                          Quantidade para agendamento:
                        </h2>
                        <p>{item.quantidade}</p>
                      </li>
                    </ul>
                  </div>
                ))}
                {agendamento?.solicitantes.map((solicitante, id) => (
                  <div key={id}>
                    <h3 className="text-lg font-bold mb-2">
                      Informações do usuário
                    </h3>
                    <ul className="text-gray-500 dark:text-gray-400">
                      <li className="flex space-x-2">
                        <h2 className="font-bold">Nome:</h2>
                        <p>{solicitante.nome}</p>
                      </li>
                      <li className="flex space-x-2">
                        <h2 className="font-bold">Email:</h2>{" "}
                        <Link
                          className="text-blue-500 hover:underline"
                          href="#"
                        >
                          {solicitante.email}
                        </Link>
                      </li>
                      <li className="flex space-x-2">
                        <h2 className="font-bold">Contato:</h2>
                        <p>{solicitante.telefone}</p>
                      </li>
                    </ul>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
};

export default ValidarAgendamento;
